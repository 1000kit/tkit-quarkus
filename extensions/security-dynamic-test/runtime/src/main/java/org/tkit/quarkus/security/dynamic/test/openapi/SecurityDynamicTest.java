package org.tkit.quarkus.security.dynamic.test.openapi;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.rootPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.vertx.http.runtime.security.ImmutablePathMatcher;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.openapi.api.OpenApiConfig;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.IOContext;
import io.smallrye.openapi.runtime.io.JsonIO;
import io.smallrye.openapi.runtime.io.OpenAPIDefinitionIO;

public class SecurityDynamicTest {

    static final String PERMISSION_CONFIG = "quarkus.http.auth.permission";

    private static final Logger log = LoggerFactory.getLogger(SecurityDynamicTest.class);

    private ImmutablePathMatcher<Set<String>> excludePaths() {

        var result = new HashMap<String, Set<String>>();

        var config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

        var keys = config.getMapKeys(PERMISSION_CONFIG)
                .keySet()
                .stream()
                .map(x -> x.substring(0, x.indexOf('.')))
                .map(x -> PERMISSION_CONFIG + "." + x)
                .collect(Collectors.toSet());

        for (var k : keys) {
            // PolicyMappingConfig
            var enabled = config.getOptionalValue(k + ".enabled", Boolean.class);
            if (enabled.isPresent() && !enabled.get()) {
                continue;
            }
            var policy = config.getValue(k + ".policy", String.class);
            if (!"permit".equals(policy)) {
                continue;
            }

            var paths = config.getOptionalValues(k + ".paths", String.class);
            if (paths.isEmpty() || paths.get().isEmpty()) {
                continue;
            }

            Set<String> tmp = null;
            var methods = config.getOptionalValues(k + ".methods", String.class);
            if (methods.isPresent()) {
                tmp = new HashSet<>(methods.get());
            }

            for (var p : paths.get()) {
                result.put(p, tmp);
            }
        }

        final var builder = ImmutablePathMatcher.<Set<String>> builder().handlerAccumulator(Set::addAll)
                .rootPath(rootPath);

        for (var e : result.entrySet()) {
            Set<String> t = new HashSet<>();
            if (e.getValue() != null) {
                t.addAll(e.getValue());
            }
            builder.addPath(e.getKey(), t);
        }

        return builder.build();
    }

    @TestFactory
    Stream<DynamicTest> testSecurity() throws Exception {

        var excludePaths = excludePaths();

        var data = loadOpenapi();

        OpenAPI openAPI = parse(new ByteArrayInputStream(data), Format.YAML,
                OpenApiConfig.fromConfig(ConfigProvider.getConfig()));

        if (openAPI.getPaths() == null || openAPI.getPaths().getPathItems() == null) {
            return Stream.empty();
        }

        List<DynamicTest> items = new ArrayList<>();
        for (var pathItem : openAPI.getPaths().getPathItems().entrySet()) {

            var path = pathItem.getKey();
            var item = pathItem.getValue();

            var pp = excludePaths.match(path);
            Set<String> excludeMethods = Set.of();
            log.debug("Security test matched: {}, methods: {}", pp.getMatched(), pp.getValue());

            if (pp.getMatched() != null && !pp.getMatched().isBlank()) {
                if (pp.getValue() == null || pp.getValue().isEmpty()) {
                    log.info("Security test skip call rule: {} method: ALL, path: {}", pp.getMatched(), path);
                    continue;
                } else {
                    excludeMethods = pp.getValue();
                }
            }

            if (item.getOperations() == null) {
                continue;
            }
            for (var tmp : item.getOperations().entrySet()) {

                var op = tmp.getValue();
                var method = tmp.getKey().name();

                if (excludeMethods.contains(method)) {
                    log.info("Security test skip call rule: {}, method: {}, path: {}", pp.getMatched(), path, method);
                    continue;
                }

                var params = new HashMap<String, Object>();
                if (op.getParameters() != null) {
                    params.putAll(op.getParameters().stream()
                            .filter(p -> p.getIn() == Parameter.In.PATH)
                            .collect(Collectors.toMap(Parameter::getName, p -> "1")));
                }

                items.add(DynamicTest.dynamicTest("Security test " + method + " " + path, () -> {

                    given()
                            .filter((requestSpec, responseSpec, ctx) -> {
                                var response = ctx.next(requestSpec, responseSpec);
                                log.info("Call {} {} [{}]", requestSpec.getMethod(), requestSpec.getURI(),
                                        response.getStatusCode());
                                return response;
                            })
                            .pathParams(params)
                            .request(method, path)
                            .then().log().ifValidationFails()
                            .statusCode(401);
                }));
            }
        }

        return items.stream();
    }

    public byte[] loadOpenapi() {
        return given()
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .extract().asByteArray();
    }

    public static OpenAPI parse(InputStream stream, Format format, OpenApiConfig config) {
        return parse(stream, format, JsonIO.newInstance(config));
    }

    private static <V, A extends V, O extends V, AB, OB> OpenAPI parse(InputStream stream, Format format,
            JsonIO<V, A, O, AB, OB> jsonIO) {
        IOContext<V, A, O, AB, OB> context = IOContext.forJson(jsonIO);
        return new OpenAPIDefinitionIO<>(context).readValue(jsonIO.fromStream(stream, format));

    }
}
