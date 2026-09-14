package com.emailConnecter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class managing in-memory caching.
 */
@Configuration
public class CacheConfig {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final int maximumSize;

    public CacheConfig(
            @Value("${secrets.cache.ttl:PT15M}") Duration ttl,
            @Value("${secrets.cache.maximum-size:100}") int maximumSize) {
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("secrets.cache.ttl must be positive");
        }
        if (maximumSize < 1) {
            throw new IllegalArgumentException("secrets.cache.maximum-size must be positive");
        }
        this.ttl = ttl;
        this.maximumSize = maximumSize;
    }

    public Optional<String> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.expiresAt() <= System.nanoTime()) {
            cache.remove(key, entry);
            return Optional.empty();
        }
        return Optional.of(entry.value());
    }

    public synchronized void put(String key, String value) {
        if (key == null || key.isBlank() || value == null || value.isBlank()) {
            throw new IllegalArgumentException("Secret cache keys and values must be nonblank");
        }
        if (cache.size() >= maximumSize && !cache.containsKey(key)) {
            var oldest = cache.entrySet().stream()
                    .min(java.util.Map.Entry.comparingByValue(
                            java.util.Comparator.comparingLong(entry -> entry.expiresAt())));
            oldest.ifPresent(entry -> cache.remove(entry.getKey(), entry.getValue()));
        }
        cache.put(key, new CacheEntry(value, System.nanoTime() + ttl.toNanos()));
    }

    public int size() {
        return cache.size();
    }

    private record CacheEntry(String value, long expiresAt) {
    }

}