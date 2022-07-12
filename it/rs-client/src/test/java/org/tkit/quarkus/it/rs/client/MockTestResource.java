package org.tkit.quarkus.it.rs.client;

import java.util.Map;

import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MockTestResource implements QuarkusTestResourceLifecycleManager {

    static MockServerContainer MOCK_SERVER;

    static MockServerClient CLIENT;

    @Override
    public Map<String, String> start() {
        MOCK_SERVER = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver").withTag("mockserver-5.12.0"));
        MOCK_SERVER.start();
        CLIENT = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());
        return Map.of(
                "quarkus.rest-client.users.url", MOCK_SERVER.getEndpoint(),
                "user/mp-rest/url", MOCK_SERVER.getEndpoint());
    }

    @Override
    public void stop() {
        MOCK_SERVER.stop();
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(CLIENT,
                new TestInjector.AnnotatedAndMatchesType(InjectMockServerClient.class, MockServerClient.class));
    }
}
