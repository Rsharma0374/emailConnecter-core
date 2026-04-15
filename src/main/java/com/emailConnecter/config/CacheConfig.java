package com.emailConnecter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class managing in-memory caching.
 */
@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    /**
     * Shared concurrent hash map used as an in-memory cache.
     */
    public static Map<String, Object> CACHE = new ConcurrentHashMap<>();

    /**
     * Checks if a key is present in the cache.
     *
     * @param key The key to check for.
     * @return true if the key is present, false otherwise.
     */
    public boolean isKeyPresent(String key) {
        boolean isPresent = CACHE.containsKey(key);
        logger.debug("Checking cache for key: {}. Present: {}", key, isPresent);
        return isPresent;
    }

    /**
     * Retrieves a value from the cache by its key.
     *
     * @param key The key to lookup.
     * @return The cached object, or null if the key is not found.
     */
    public Object getValueByKey(String key) {
        Object value = CACHE.get(key);
        logger.debug("Retrieving value from cache for key: {}. Found: {}", key, value != null);
        return value;
    }

}