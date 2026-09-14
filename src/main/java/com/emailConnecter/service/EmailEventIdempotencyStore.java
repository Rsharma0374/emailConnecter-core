package com.emailConnecter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prevents concurrent and repeated processing of an event within this consumer instance.
 */
@Component
public class EmailEventIdempotencyStore {

    private final ConcurrentHashMap<UUID, Long> processed = new ConcurrentHashMap<>();
    private final long retentionNanos;
    private final int maximumSize;

    public EmailEventIdempotencyStore(
            @Value("${spring.kafka.consumer.idempotency-retention:PT24H}") Duration retention,
            @Value("${spring.kafka.consumer.idempotency-maximum-size:100000}") int maximumSize) {
        if (retention.isNegative() || retention.isZero() || maximumSize < 1) {
            throw new IllegalArgumentException("Kafka idempotency settings must be positive");
        }
        this.retentionNanos = retention.toNanos();
        this.maximumSize = maximumSize;
    }

    public synchronized boolean claim(UUID requestId) {
        long now = System.nanoTime();
        processed.entrySet().removeIf(entry -> entry.getValue() <= now);
        if (processed.containsKey(requestId)) {
            return false;
        }
        if (processed.size() >= maximumSize) {
            processed.entrySet().stream()
                    .min(java.util.Map.Entry.comparingByValue())
                    .ifPresent(entry -> processed.remove(entry.getKey(), entry.getValue()));
        }
        return processed.putIfAbsent(requestId, now + retentionNanos) == null;
    }

    public void release(UUID requestId) {
        processed.remove(requestId);
    }
}
