package com.apurva.chat.auth;

/** Body of POST /api/auth/register. */
public record RegisterRequest(String username, String password, String displayName) {
}
