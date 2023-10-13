package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

class RedisConnectionFactory {

    public RedisClient createClient(RedisURI redisURI) {
        return RedisClient.create(redisURI);
    }

    public StatefulRedisConnection<String, String> createConnection(RedisClient client) {
        return client.connect();
    }
}
