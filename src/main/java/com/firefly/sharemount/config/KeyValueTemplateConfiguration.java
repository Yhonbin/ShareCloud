package com.firefly.sharemount.config;

import com.firefly.sharemount.component.KeyValueTemplate;
import com.firefly.sharemount.component.impl.KeyValueTemplateRedisImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class KeyValueTemplateConfiguration {
    @Bean
    public KeyValueTemplate keyValueTemplate(){
        return new KeyValueTemplateRedisImpl();
    }
}
