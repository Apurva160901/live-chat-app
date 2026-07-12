package com.apurva.chat.auth;

/** Body of POST /api/auth/register. `email` is optional (used for password reset). */
public record RegisterRequest(String username, String password, String displayName, String email) {
}
