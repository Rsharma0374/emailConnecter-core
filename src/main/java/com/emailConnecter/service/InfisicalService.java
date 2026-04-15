package com.emailConnecter.service;

import com.emailConnecter.config.CacheConfig;
import com.emailConnecter.config.InfisicalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class for interacting with Infisical to retrieve secrets.
 * Implements an in-memory caching mechanism to optimize repeated secret fetches.
 */
@Service
public class InfisicalService {

    private static final Logger logger = LoggerFactory.getLogger(InfisicalService.class);

    /**
     * Retrieves a secret by its name. Checks the local cache first, 
     * and if not found, queries Infisical.
     *
     * @param secretName the name of the secret to retrieve.
     * @return the value of the secret, or null if it cannot be found.
     */
    public String getSecret(String secretName) {
        logger.debug("Requesting secret: {}", secretName);
        try {
            if (CacheConfig.CACHE.containsKey(secretName)) {
                logger.debug("Secret '{}' found in cache", secretName);
                return String.valueOf(CacheConfig.CACHE.get(secretName));
            }
            
            logger.info("Secret '{}' not in cache. Fetching from Infisical...", secretName);
            Map<String, Object> configMap = InfisicalConfig.fetchConfig("amazon_ses");

            if (configMap == null || configMap.isEmpty()) {
                throw new RuntimeException("ConfigMap is missing or empty.");
            }

            // ✅ Store ALL entries in cache
            for (Map.Entry<String, Object> entry : configMap.entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());

                CacheConfig.CACHE.put(key, value);
            }
            
            logger.debug("Cache populated with {} entries from Infisical", configMap.size());

            // Return requested secret
            return String.valueOf(CacheConfig.CACHE.get(secretName));

        } catch (Exception e) {
            logger.error("Failed to fetch secret {} from Infisical", secretName, e);
        }
        return null;
    }
}
