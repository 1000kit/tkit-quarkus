package org.tkit.quarkus.rs.interceptor;

import java.util.Optional;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.ConfigProvider;

@Provider
public class TransferEncodingFilter implements ClientResponseFilter {

    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) {
        Optional<Boolean> activateFilter = ConfigProvider.getConfig().getOptionalValue("tkit.rs.filter.transfer-encoding",
                Boolean.class);
        if (activateFilter.isEmpty() || activateFilter.get()) {
            String teHeaderValue = clientResponseContext.getHeaderString(TRANSFER_ENCODING);
            if (teHeaderValue != null && teHeaderValue.contains("chunked")) {
                clientResponseContext.getHeaders().remove(TRANSFER_ENCODING);
            }
        }
    }
}
