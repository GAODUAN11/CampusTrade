package com.campustrade.gateway.repository;

import com.campustrade.gateway.model.auth.AuthSession;
import com.campustrade.gateway.model.auth.AuthUserRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAuthStore {
    private final Map<Long, AuthUserRecord> userById = new ConcurrentHashMap<>();
    private final Map<String, Long> usernameIndex = new ConcurrentHashMap<>();
    private final Map<String, Long> phoneIndex = new ConcurrentHashMap<>();
    private final Map<String, Long> emailIndex = new ConcurrentHashMap<>();
    private final Map<String, AuthSession> sessionByToken = new ConcurrentHashMap<>();
    /**
     * Keep gateway-local auth IDs far away from seeded user-service IDs (10001+)
     * to avoid accidental identity collisions in cross-service aggregation.
     */
    private final AtomicLong userIdGenerator = new AtomicLong(1_000_000L);

    private final Object userLock = new Object();

    public Optional<AuthUserRecord> findByUsername(String username) {
        if (!hasText(username)) {
            return Optional.empty();
        }
        Long userId = usernameIndex.get(normalize(username));
        return findByUserId(userId);
    }

    public Optional<AuthUserRecord> findByPhone(String phone) {
        if (!hasText(phone)) {
            return Optional.empty();
        }
        Long userId = phoneIndex.get(normalize(phone));
        return findByUserId(userId);
    }

    public Optional<AuthUserRecord> findByEmail(String email) {
        if (!hasText(email)) {
            return Optional.empty();
        }
        Long userId = emailIndex.get(normalize(email));
        return findByUserId(userId);
    }

    public Optional<AuthUserRecord> findByAccount(String account) {
        Optional<AuthUserRecord> byUsername = findByUsername(account);
        if (byUsername.isPresent()) {
            return byUsername;
        }
        Optional<AuthUserRecord> byPhone = findByPhone(account);
        if (byPhone.isPresent()) {
            return byPhone;
        }
        return findByEmail(account);
    }

    public AuthUserRecord saveNewUser(AuthUserRecord source) {
        synchronized (userLock) {
            AuthUserRecord target = copyUser(source);
            Long userId = userIdGenerator.incrementAndGet();
            target.setUserId(userId);
            target.setCreatedAt(LocalDateTime.now());
            target.setUpdatedAt(target.getCreatedAt());

            userById.put(userId, copyUser(target));
            usernameIndex.put(normalize(target.getUsername()), userId);
            if (hasText(target.getPhone())) {
                phoneIndex.put(normalize(target.getPhone()), userId);
            }
            if (hasText(target.getEmail())) {
                emailIndex.put(normalize(target.getEmail()), userId);
            }
            return copyUser(target);
        }
    }

    public void saveSession(AuthSession source) {
        AuthSession target = copySession(source);
        sessionByToken.put(target.getToken(), target);
    }

    public Optional<AuthSession> findSessionByToken(String token) {
        if (!hasText(token)) {
            return Optional.empty();
        }
        AuthSession session = sessionByToken.get(token.trim());
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(copySession(session));
    }

    public void removeSessionByToken(String token) {
        if (!hasText(token)) {
            return;
        }
        sessionByToken.remove(token.trim());
    }

    public void cleanupExpiredSessions(LocalDateTime now) {
        sessionByToken.entrySet().removeIf(entry -> entry.getValue().getExpiresAt().isBefore(now));
    }

    private Optional<AuthUserRecord> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        AuthUserRecord record = userById.get(userId);
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(copyUser(record));
    }

    private AuthUserRecord copyUser(AuthUserRecord source) {
        AuthUserRecord target = new AuthUserRecord();
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setNickname(source.getNickname());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setSchool(source.getSchool());
        target.setCampus(source.getCampus());
        target.setVerified(source.getVerified());
        target.setStatus(source.getStatus());
        target.setPasswordHash(source.getPasswordHash());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        return target;
    }

    private AuthSession copySession(AuthSession source) {
        AuthSession target = new AuthSession();
        target.setToken(source.getToken());
        target.setUserId(source.getUserId());
        target.setRememberMe(source.getRememberMe());
        target.setIssuedAt(source.getIssuedAt());
        target.setExpiresAt(source.getExpiresAt());
        return target;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
