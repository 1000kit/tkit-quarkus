package org.tkit.quarkus.rs.interceptor;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class TransferEncodingFilter implements ClientResponseFilter {

    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) {
        String teHeaderValue = clientResponseContext.getHeaderString(TRANSFER_ENCODING);
        if (teHeaderValue != null && teHeaderValue.contains("chunked")) {
            clientResponseContext.getHeaders().remove(TRANSFER_ENCODING);
        }
    }
}
