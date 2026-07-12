package com.apurva.chat.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit tests for JwtService — no Spring context, no database, just the logic.
 */
class JwtServiceTest {

    // A test secret at least 32 bytes long (required for HS-family signing).
    private final JwtService jwt = new JwtService(
            "test-secret-key-that-is-definitely-long-enough-0123456789", 3_600_000L);

    @Test
    void generatesTokenAndReadsUsernameBack() {
        String token = jwt.generateToken("apurva");
        assertTrue(jwt.isValid(token));
        assertEquals("apurva", jwt.extractUsername(token));
    }

    @Test
    void rejectsGarbageToken() {
        assertFalse(jwt.isValid("this.is.not-a-real-token"));
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtService other = new JwtService(
                "a-completely-different-secret-key-9876543210-abcdef", 3_600_000L);
        String foreignToken = other.generateToken("apurva");
        assertFalse(jwt.isValid(foreignToken));
    }
}
