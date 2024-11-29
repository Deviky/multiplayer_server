package com.cybersport.room.config;

import com.cybersport.room.api.v1.dto.PlayerGameData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PlayerGameData> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PlayerGameData> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Устанавливаем сериализаторы
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
