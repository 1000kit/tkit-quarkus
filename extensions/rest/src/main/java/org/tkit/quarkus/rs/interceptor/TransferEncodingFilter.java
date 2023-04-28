package org.tkit.quarkus.rs.interceptor;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

public class TransferEncodingFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) {
        clientResponseContext.getHeaders().remove("Transfer-Encoding");
    }
}
