package org.tkit.quarkus.rs.interceptors;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Priority(Priorities.AUTHENTICATION)
public class OpenIDConnectInterceptor implements ClientRequestFilter, ClientResponseFilter {

    private static final String AUTH_RETRY_FLAG = "AUTH_RETRY_FLAG";

    final private Map<String, String> clientConfiguration;

    private static final String ACCESS_TOKEN_SCOPE = "openid";

    private AtomicReference<TokenState> currentTokenState = new AtomicReference<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenIDConnectInterceptor.class);

    public OpenIDConnectInterceptor() {
        Config config = ConfigProvider.getConfig();
        clientConfiguration = Stream.of("CLIENT_ID", "CLIENT_SECRET", "RHSSO_TOKEN_ENDPOINT")
                .collect(
                        HashMap::new,
                        (map, envVarName) -> map.put(envVarName, config.getOptionalValue(envVarName, String.class).orElse(null)),
                        HashMap::putAll);
    }

    @Override
    public void filter(ClientRequestContext crc) throws IOException {
        if (crc.getUri().toString().equals(clientConfiguration.get("RHSSO_TOKEN_ENDPOINT"))) {
            return;
        }
        if (clientConfiguration.containsValue(null)) {
            String error = "OPENID client configuration not valid, missing env variables : [" + clientConfiguration.entrySet().stream()
                    .filter(e -> e.getValue() == null)
                    .map(e -> e.getKey())
                    .collect(Collectors.joining(",")) + "]";
            LOGGER.error(error + ", aborting request processing");
            crc.abortWith(javax.ws.rs.core.Response.status(Response.Status.PRECONDITION_FAILED).entity(error).build());
            return;
        }
        Client client = ClientBuilder.newClient();;
        TokenState tokenState = currentTokenState.get();
        if (tokenState == null) {
            tokenState = requestInitialTokens(client);
            currentTokenState.set(tokenState);
        }
        long tokenIssueDate = tokenState.getIssueDate();
        long tokenTTL = tokenState.getTokenTTL();
        if (System.currentTimeMillis() > (tokenIssueDate + tokenTTL * 1000)) {
            tokenState = refreshToken(crc.getClient(), tokenState);
            currentTokenState.set(tokenState);
        }
        Object currentToken = tokenState.getToken();
        crc.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + currentToken);
    }

    @Override
    public void filter(ClientRequestContext requestCtx, ClientResponseContext responseCtx) throws IOException {
        if (requestCtx.getUri().toString().equals(clientConfiguration.get("RHSSO_TOKEN_ENDPOINT"))) {
            return;
        }
        if (requestCtx.getProperty(AUTH_RETRY_FLAG) != null) {
            //we only retry once
            return;
        }
        if (responseCtx.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            boolean wasTokenSentBefore = requestCtx.getHeaderString(HttpHeaders.AUTHORIZATION).toLowerCase().contains("bearer");
            if (wasTokenSentBefore) { //attempt refresh
                TokenState state = currentTokenState.get();
                requestCtx.setProperty(AUTH_RETRY_FLAG, true);
                TokenState newTokenState = refreshToken(requestCtx.getClient(), state);
                currentTokenState.set(newTokenState);
                repeatRequestWithAuth(requestCtx, responseCtx, newTokenState.getToken());
            }
        }
    }

    private TokenState requestInitialTokens(Client client) throws UnsupportedEncodingException {
        LOGGER.info("Requesting access token");
        Form form = new Form()
                .param("grant_type", "client_credentials")
                .param("scope", ACCESS_TOKEN_SCOPE);

        TokenState newState = requestAccessTokenAndUpdateState(client, form);
        LOGGER.info("Access token granted, ttl: {}, expiration: {}", newState.getTokenTTL(), newState.getTokenExpirationDate());
        return newState;
    }

    private TokenState requestAccessTokenAndUpdateState(Client client, Form form) throws UnsupportedEncodingException {
        WebTarget target = client.target(clientConfiguration.get("RHSSO_TOKEN_ENDPOINT"));

        Map<String, Object> response = target.request()
                .header(HttpHeaders.AUTHORIZATION, getClientCredentialsAuth(clientConfiguration.get("CLIENT_ID"), clientConfiguration.get("CLIENT_SECRET")))
                .post(Entity.form(form), new GenericType<Map<String, Object>>() {});
        AccessToken token = parseAccessToken(response);
        return updateClientTokenState(token);
    }

    private AccessToken parseAccessToken(Map<String, Object> data) {
        AccessToken token = new AccessToken();
        Long longValue = getLongValue(data.get("expires_in"));
        if (longValue != null) {
            token.setExpiresIn(longValue);
        }
        longValue = getLongValue(data.get("refresh_expires_in"));
        if (longValue != null) {
            token.setRefreshExpiresIn(longValue);
        }
        token.setToken((String) data.get("access_token"));
        token.setRefreshToken((String) data.get("refresh_token"));
        return token;
    }

    private Long getLongValue(Object value) {
        if (value != null) {
            if (value instanceof Integer) {
                return (long) (int) value;
            }
            if (value instanceof Long) {
                return (long) value;
            }
        }
        return null;
    }

    private TokenState refreshToken(Client client, TokenState state) throws UnsupportedEncodingException {
        LOGGER.info("Refreshing access token, refresh expiration: {}", state.getRefreshTokenExpirationDate());
        Form form = new Form()
                .param("grant_type", "refresh_token")
                .param("scope", ACCESS_TOKEN_SCOPE)
                .param("refresh_token", state.getRefreshToken());
        try {
            TokenState newState = requestAccessTokenAndUpdateState(client, form);
            LOGGER.info("Access token refreshed successfully, ttl: {}", newState.getTokenTTL());
            return newState;
        } catch (Exception e) {
            LOGGER.warn("Refresh token request failed, trying to request initial token", e);
            //refresh failed, try request new token
            return requestInitialTokens(client);
        }
    }

    /**
     * Repeat http call, just change the authorization header
     *
     * @param request  original req
     * @param response original response
     * @param token    newtoken
     * @return true if authorized
     */
    private boolean repeatRequestWithAuth(ClientRequestContext request, ClientResponseContext response, String token) {

        Client client = request.getClient();

        String method = request.getMethod();
        MediaType mediaType = request.getMediaType();
        URI lUri = request.getUri();

        WebTarget resourceTarget = client.target(lUri);

        Invocation.Builder builder = resourceTarget.request(mediaType);

        MultivaluedMap<String, Object> newHeaders = new MultivaluedHashMap<String, Object>();

        for (Map.Entry<String, List<Object>> entry : request.getHeaders().entrySet()) {
            if (HttpHeaders.AUTHORIZATION.equals(entry.getKey())) {
                continue;
            }
            newHeaders.put(entry.getKey(), entry.getValue());
        }

        newHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        builder.headers(newHeaders);
        builder.property(AUTH_RETRY_FLAG, true);
        Invocation invocation;
        if (request.getEntity() == null) {
            invocation = builder.build(method);
        } else {
            invocation = builder.build(method, Entity.entity(request.getEntity(), request.getMediaType()));
        }
        Response nextResponse = invocation.invoke();

        if (nextResponse.hasEntity()) {
            response.setEntityStream(nextResponse.readEntity(InputStream.class));
        }
        MultivaluedMap<String, String> headers = response.getHeaders();
        headers.clear();
        headers.putAll(nextResponse.getStringHeaders());
        response.setStatus(nextResponse.getStatus());

        return response.getStatus() != Response.Status.UNAUTHORIZED.getStatusCode();
    }

    private TokenState updateClientTokenState(AccessToken tokenResp) {
        TokenState state = new TokenState();
        state.setIssueDate(System.currentTimeMillis());
        if (tokenResp.getRefreshToken() != null) {
            state.setRefreshToken(tokenResp.getRefreshToken());
        }
        state.setTokenTTL(tokenResp.getExpiresIn());
        state.setToken(tokenResp.getToken());

        return state;
    }

    public class TokenState {
        private String token;
        private long issueDate;
        private long tokenTTL;
        private long refreshTokenTTL;
        private String refreshToken;
        private String tokenType;

        public Date getTokenExpirationDate() {
            return new Date(this.issueDate + tokenTTL * 1000);
        }

        public Date getRefreshTokenExpirationDate() {
            return new Date(this.issueDate + refreshTokenTTL * 1000);
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(long issueDate) {
            this.issueDate = issueDate;
        }

        public long getTokenTTL() {
            return tokenTTL;
        }

        public void setTokenTTL(long tokenTTL) {
            this.tokenTTL = tokenTTL;
        }

        public long getRefreshTokenTTL() {
            return refreshTokenTTL;
        }

        public void setRefreshTokenTTL(long refreshTokenTTL) {
            this.refreshTokenTTL = refreshTokenTTL;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }

    private String getClientCredentialsAuth(String clientId, String clientPassword) throws UnsupportedEncodingException {
        String tmp = clientId + ":" + clientPassword;
        return "Basic " + Base64.getEncoder().encodeToString(tmp.getBytes(StandardCharsets.UTF_8));
    }

}
