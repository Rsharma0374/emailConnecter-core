package com.emailConnecter.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class providing helper methods for common tasks.
 */
public class Helper {

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    /**
     * Fetches properties from a file at the given path.
     *
     * @param userAuthPropertiesPath the absolute path to the properties file.
     * @return a Properties object loaded with the file's content, or null if an error occurs.
     */
    public static Properties fetchProperties(String userAuthPropertiesPath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(userAuthPropertiesPath)) {
            properties.load(fis);
            logger.info("Successfully loaded properties from: {}", userAuthPropertiesPath);
            return properties;
        } catch (IOException e) {
            logger.error("Failed to load properties from: {}", userAuthPropertiesPath, e);
            return null;
        }
    }

    /**
     * Masks a string by replacing characters at odd indices with asterisks.
     *
     * <p>Pattern: Characters at even indices (0, 2, 4...) remain visible,
     * odd indices (1, 3, 5...) are replaced with '*'.</p>
     *
     * <p>Examples:
     * <ul>
     *   <li>"password" → "p*s*w*r*"</li>
     *   <li>"12345" → "1*3*5"</li>
     *   <li>"" → ""</li>
     *   <li>null → null</li>
     * </ul>
     * </p>
     *
     * @param value the string to mask (can be null or empty)
     * @return masked string with odd-indexed characters replaced by '*',
     *         or the original value if null/empty
     */
    public static String maskString(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        int n = value.length();
        char[] result = new char[n];

        for (int i = 0; i < n; i++) {
            result[i] = (i % 2 == 0) ? value.charAt(i) : '*';
        }

        return new String(result);
    }

}
