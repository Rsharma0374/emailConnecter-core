package com.emailConnecter.config;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.utility.ResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.*;

@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    public static Map<String , Object> CACHE = new ConcurrentHashMap<>();

    private static final Map<String, String> guardianSmtp = new HashMap<>();
    private static final Map<String, String> brevoSmtp = new HashMap<>();

    // Scheduler for cache cleanup
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        try {
            Map properties = InfisicalConfig.fetchConfig("Email");
            if (properties != null) {
                //Guardian Service SMTP
                guardianSmtp.put("host", properties.get(Constant.GSERVICE_HOST).toString());
                guardianSmtp.put("port", properties.get(Constant.GSERVICE_PORT).toString());
                guardianSmtp.put("username", properties.get(Constant.GSERVICE_USERNAME).toString());
                guardianSmtp.put("password", properties.get(Constant.GSERVICE_PASSWORD).toString());

                //Brevo service SMTP
                brevoSmtp.put("host", properties.get(Constant.BREVO_HOST).toString());
                brevoSmtp.put("port", properties.get(Constant.BREVO_PORT).toString());
                brevoSmtp.put("username", properties.get(Constant.BREVO_USERNAME).toString());
                brevoSmtp.put("password", properties.get(Constant.BREVO_PASSWORD).toString());

                long expiryTime = ResponseUtility.dayEndExpiryTime();
                CacheConfig.put("guardianSmtp", guardianSmtp, expiryTime);
                CacheConfig.put("brevoSmtp", brevoSmtp, expiryTime);
                CacheConfig.CACHE.put(Constant.FROM, properties.get(Constant.FROM).toString());
                CacheConfig.CACHE.put(Constant.BREVO_API_KEY, properties.get(Constant.BREVO_API_KEY).toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Puts a value in the cache with an optional expiration time.
     *
     * @param key The key to store in the cache.
     * @param value The value to store in the cache.
     * @param expirationTimeInSeconds Time in seconds after which the cache entry is removed. If <= 0, no expiration.
     */
    public static void put(String key, Object value, long expirationTimeInSeconds) {
        CACHE.put(key, value);

        if (expirationTimeInSeconds > 0) {
            scheduler.schedule(() -> {
                CACHE.remove(key);
                logger.warn("Key [" + key + "] has been removed from the cache.");
            }, expirationTimeInSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * Gets a value from the cache.
     *
     * @param key The key to retrieve.
     * @return The cached value or null if not found.
     */
    public static Object get(String key) {
        return CACHE.get(key);
    }

    public static void clearCacheByKey(String key) {
        if (CACHE.containsKey(key)) {
            CACHE.remove(key);
            logger.warn("Key [" + key + "] has been removed from the cache.");
        } else {
            logger.warn("Key [" + key + "] does not exist in the cache.");
        }
    }

}
