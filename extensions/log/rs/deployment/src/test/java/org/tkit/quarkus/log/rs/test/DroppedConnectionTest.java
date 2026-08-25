package org.tkit.quarkus.log.rs.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.SlowRestController;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.http.TestHTTPResource;

/**
 * Since quarkusio/quarkus#50990 (Quarkus 3.31.1) the REST pipeline is cancelled when the client
 * drops the connection, which skips every {@code ContainerResponseFilter}. The interceptor opts
 * back in with {@code @Cancellable(false)}, and it has to cope with the fact that no response may
 * exist for such a request.
 *
 * @see <a href="https://github.com/quarkusio/quarkus/issues/54889">quarkusio/quarkus#54889</a>
 */
public class DroppedConnectionTest extends AbstractTest {

    private static final long LOG_WAIT_MS = 15_000;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(SlowRestController.class)
                    .addAsResource("default.properties", "application.properties"));

    @TestHTTPResource("/slow/response")
    URL responseUrl;

    @TestHTTPResource("/slow/entity")
    URL entityUrl;

    @Test
    public void endLogIsWrittenWhenClientDropsConnectionTest() throws Exception {
        dropConnection(responseUrl);

        // the response was created by the endpoint itself, so its status is still available
        awaitLogLine("GET /slow/response [200] [");
    }

    @Test
    public void endLogIsWrittenWhenClientDropsConnectionAndNoResponseExistsTest() throws Exception {
        dropConnection(entityUrl);

        // no response was created for the dropped request, so there is no status to log
        awaitLogLine("GET /slow/entity [null] [");
    }

    private void dropConnection(URL url) throws Exception {
        Socket socket = new Socket(url.getHost(), url.getPort());
        // force a RST on close, which is what an abandoned client or a gateway timeout looks like
        socket.setSoLinger(true, 0);

        OutputStream out = socket.getOutputStream();
        out.write(("GET " + url.getPath() + " HTTP/1.1\r\n"
                + "Host: " + url.getHost() + ":" + url.getPort() + "\r\n"
                + "Connection: close\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.flush();

        // give the server time to start processing, then abandon the request
        Thread.sleep(300);
        socket.close();
    }

    private void awaitLogLine(String text) throws InterruptedException {
        long deadline = System.currentTimeMillis() + LOG_WAIT_MS;
        String[] lines = new String[] {};
        while (System.currentTimeMillis() < deadline) {
            lines = logLines();
            for (String line : lines) {
                if (line.contains(text)) {
                    return;
                }
            }
            Thread.sleep(100);
        }
        fail("Assert log contains \n ==> text: `" + text + "` \n ==> lines: \n" + String.join("\t\n", lines) + "\n");
    }

}
