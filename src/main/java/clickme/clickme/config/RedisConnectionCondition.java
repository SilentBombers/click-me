package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class RedisConnectionCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        RedisURI redisURI = RedisURI.builder()
                .withHost(context.getEnvironment().getProperty("spring.data.redis.host"))
                .withPort(Integer.parseInt(context.getEnvironment().getProperty("spring.data.redis.port")))
                .build();
        RedisClient redisClient = RedisClient.create(redisURI);
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            String result = connection.sync().ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            return false;
        } finally {
            redisClient.shutdown();
        }
    }
}
