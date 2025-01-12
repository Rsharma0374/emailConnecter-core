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
    private static final String EMAIL_CONNECTOR_PROPERTIES_PATH = "/opt/configs/emailConnector.properties";

    private static final Map<String, String> guardianSmtp = new HashMap<>();
    private static final Map<String, String> brevoSmtp = new HashMap<>();

    // Scheduler for cache cleanup
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        Properties properties = ResponseUtility.fetchProperties(EMAIL_CONNECTOR_PROPERTIES_PATH);
        if (null != properties) {

            //Guardian Service SMTP
            guardianSmtp.put("host", properties.getProperty(Constant.GSERVICE_HOST));
            guardianSmtp.put("port", properties.getProperty(Constant.GSERVICE_PORT));
            guardianSmtp.put("username", properties.getProperty(Constant.GSERVICE_USERNAME));
            guardianSmtp.put("password", properties.getProperty(Constant.GSERVICE_PASSWORD));

            //Brevo service SMTP
            brevoSmtp.put("host", properties.getProperty(Constant.BREVO_HOST));
            brevoSmtp.put("port", properties.getProperty(Constant.BREVO_PORT));
            brevoSmtp.put("username", properties.getProperty(Constant.BREVO_USERNAME));
            brevoSmtp.put("password", properties.getProperty(Constant.BREVO_PASSWORD));

            long expiryTime = ResponseUtility.dayEndExpiryTime();
            CacheConfig.put("guardianSmtp", guardianSmtp, expiryTime);
            CacheConfig.put("brevoSmtp", brevoSmtp, expiryTime);
            CacheConfig.CACHE.put(Constant.FROM, properties.getProperty(Constant.FROM));
            CacheConfig.CACHE.put(Constant.BREVO_API_KEY, properties.getProperty(Constant.BREVO_API_KEY));
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
