package com.apurva.chat.auth;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Issues short-lived, single-use password-reset tokens.
 *
 * In PRODUCTION the token would be emailed to the user and stored in the DB.
 * For this offline demo we keep them in memory and hand the token back to the
 * client. The security idea is the same: a random, time-limited, one-time token.
 */
@Service
public class PasswordResetService {

    private record Entry(String username, Instant expiresAt) {
    }

    private static final long TTL_SECONDS = 15 * 60; // 15 minutes
    private final ConcurrentHashMap<String, Entry> tokens = new ConcurrentHashMap<>();

    public String createToken(String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new Entry(username, Instant.now().plusSeconds(TTL_SECONDS)));
        return token;
    }

    /** Returns the username if the token is valid + unexpired (and consumes it); else null. */
    public String consume(String token) {
        Entry entry = tokens.remove(token);
        if (entry == null || entry.expiresAt().isBefore(Instant.now())) {
            return null;
        }
        return entry.username();
    }
}
