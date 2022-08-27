package tv.vradio.vradiotvserver;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisExtension implements BeforeAllCallback, AfterAllCallback {
    private GenericContainer<?> redis;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        redis = new GenericContainer<>(DockerImageName.parse("redis:6.2.7-bullseye")).withExposedPorts(6379);

        redis.start();

        System.setProperty("spring.redis.host", redis.getHost());
        System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

    }
}
