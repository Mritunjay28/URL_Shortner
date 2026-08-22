package com.Project.URL.Shortner.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    // store bucket for each client
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ipAddress){
        return cache.computeIfAbsent(ipAddress,this::createNewBucket);
    }

    private Bucket createNewBucket(String key) {
        // allow 10 request
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10,Duration.ofMinutes(2))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
