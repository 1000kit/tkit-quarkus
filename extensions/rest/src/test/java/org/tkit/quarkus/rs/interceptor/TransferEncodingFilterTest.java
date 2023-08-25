package org.tkit.quarkus.rs.interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferEncodingFilterTest {

    @Mock
    ClientResponseContext clientResponseContextMock;

    TransferEncodingFilter transferEncodingFilter = new TransferEncodingFilter();

    @AfterEach
    void mockReset() {
        Mockito.reset(clientResponseContextMock);
    }

    @Test
    void filterChunked() {
        // given
        Mockito.when(clientResponseContextMock.getHeaderString("Transfer-Encoding")).thenReturn("chunked");

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("Transfer-Encoding","chunked");
        Mockito.when(clientResponseContextMock.getHeaders()).thenReturn(headers);

        // when
        transferEncodingFilter.filter(null, clientResponseContextMock);

        // then
        assertFalse(headers.containsKey("Transfer-Encoding"));
    }

    @Test
    void filterOther() {
        // given
        Mockito.when(clientResponseContextMock.getHeaderString("Transfer-Encoding")).thenReturn("compress");

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("Transfer-Encoding","compress");
        Mockito.when(clientResponseContextMock.getHeaders()).thenReturn(headers);

        // when
        transferEncodingFilter.filter(null, clientResponseContextMock);

        // then
        assertTrue(headers.containsKey("Transfer-Encoding"));
        assertEquals("compress", headers.getFirst("Transfer-Encoding"));
    }

    @Test
    void filterNoHeader() {
        // given
        Mockito.when(clientResponseContextMock.getHeaderString("Transfer-Encoding")).thenReturn(null);

        // when
        transferEncodingFilter.filter(null, clientResponseContextMock);

        // then
        Mockito.verify(clientResponseContextMock, Mockito.never()).getHeaders();
    }

}