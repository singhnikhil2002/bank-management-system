package com.bank.cms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // ── Generic Operations ─────────────────────────────────────────

    public void set(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ── Token Blacklist ────────────────────────────────────────────

    public void blacklistToken(String token, long ttlSeconds) {
        // key = "blacklist:token"  value = "true"
        set("blacklist:" + token, "true", ttlSeconds);
    }

    public boolean isTokenBlacklisted(String token) {
        return exists("blacklist:" + token);
    }

    // ── Rate Limiting ──────────────────────────────────────────────

    public int incrementLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        Long count = redisTemplate.opsForValue().increment(key);

        // Set expiry only on first attempt
        if (count != null && count == 1) {
            redisTemplate.expire(key, 15, TimeUnit.MINUTES);
        }
        return count != null ? count.intValue() : 1;
    }

    public void resetLoginAttempts(String email) {
        delete("login:attempts:" + email);
    }

    public boolean isLoginLocked(String email) {
        return exists("login:locked:" + email);
    }

    public void lockLogin(String email) {
        set("login:locked:" + email, "true", 15 * 60); // 15 minutes
    }

    // ── Balance Cache ──────────────────────────────────────────────

    public void cacheBalance(String accountNumber, Double balance) {
        set("balance:" + accountNumber, String.valueOf(balance), 60); // 60 seconds
    }

    public Double getCachedBalance(String accountNumber) {
        String value = get("balance:" + accountNumber);
        return value != null ? Double.parseDouble(value) : null;
    }

    public void evictBalanceCache(String accountNumber) {
        delete("balance:" + accountNumber);
    }
}