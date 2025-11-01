package com.userservice.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableCaching
@Profile("!test")
public class RedisConfig {
    //время жизни кэша(в минутах)
    private static final Duration TTL_DEFAULT = Duration.ofMinutes(10);
    private static final Duration TTL_USERS = Duration.ofMinutes(5);
    private static final Duration TTL_CARDS = Duration.ofMinutes(30);


    //короче конфигурируем соединение
    @Bean
    JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    //создаем основу для взаимодействия редиса с программой
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    //настройки кэша
    @Bean
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        //обычные настройки
        RedisCacheConfiguration config = redisCacheConfiguration(TTL_DEFAULT);

        //хранилище настроек, которые для областей кэша
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        //настройки области пользователей
        //добавление настроек в хранилище
        cacheConfigurations.put("users", redisCacheConfiguration(TTL_USERS));

        //настройки области карт
        //добавление настроек в хранилище
        cacheConfigurations.put("cards", redisCacheConfiguration(TTL_CARDS));

        //конфигурация настроек
        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }


    public RedisCacheConfiguration redisCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

}
