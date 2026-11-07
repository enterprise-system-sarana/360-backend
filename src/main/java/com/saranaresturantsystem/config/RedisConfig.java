package com.saranaresturantsystem.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class RedisConfig implements CachingConfigurer {

    private ObjectMapper createRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule pageModule = new SimpleModule("SpringDataPageModule");
        PageImplDeserializer pageDeserializer = new PageImplDeserializer();
        pageModule.addDeserializer(PageImpl.class, pageDeserializer);
        pageModule.addDeserializer(Page.class, (JsonDeserializer) pageDeserializer);
        mapper.registerModule(pageModule);

        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    public static class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {
        @Override
        public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = mapper.readTree(p);

            List<Object> content = new ArrayList<>();
            JsonNode contentNode = node.get("content");
            if (contentNode != null && contentNode.isArray()) {
                for (JsonNode itemNode : contentNode) {
                    content.add(mapper.treeToValue(itemNode, Object.class));
                }
            }

            int pageNumber = 0;
            int pageSize = 10;
            long totalElements = content.size();

            if (node.hasNonNull("number")) {
                pageNumber = node.get("number").asInt(0);
            } else if (node.hasNonNull("pageNumber")) {
                pageNumber = node.get("pageNumber").asInt(0);
            }

            if (node.hasNonNull("size")) {
                pageSize = node.get("size").asInt(10);
            } else if (node.hasNonNull("pageSize")) {
                pageSize = node.get("pageSize").asInt(10);
            }

            if (node.hasNonNull("totalElements")) {
                totalElements = node.get("totalElements").asLong(content.size());
            } else if (node.hasNonNull("total")) {
                totalElements = node.get("total").asLong(content.size());
            }

            JsonNode pageableNode = node.get("pageable");
            if (pageableNode != null && pageableNode.isObject()) {
                if (pageableNode.hasNonNull("pageNumber")) {
                    pageNumber = pageableNode.get("pageNumber").asInt(pageNumber);
                }
                if (pageableNode.hasNonNull("pageSize")) {
                    int ps = pageableNode.get("pageSize").asInt(pageSize);
                    if (ps > 0) pageSize = ps;
                }
            }

            if (pageSize <= 0) {
                pageSize = 10;
            }

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return new PageImpl<>(content, pageable, totalElements);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer)
                );

        // Specific TTL configuration per cache group
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // Master / Catalog data - 2 hours
        cacheConfigs.put("categories", defaultCacheConfig.entryTtl(Duration.ofHours(2)));

        // Sales data - 30 minutes
        cacheConfigs.put("sales", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));

        // Reports - 15 minutes TTL (fast aggregated caching with auto-refresh)
        cacheConfigs.put("report:sales", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:purchases", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:payments:sales", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:payments:purchases", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:stocks", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:profit-loss", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("report:banks", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis cache GET failed for cache='{}', key='{}'. Falling back to database: {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis cache PUT failed for cache='{}', key='{}': {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis cache EVICT failed for cache='{}', key='{}': {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis cache CLEAR failed for cache='{}': {}",
                        cache != null ? cache.getName() : "unknown", exception.getMessage());
            }
        };
    }
}
