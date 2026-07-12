package com.apurva.chat.auth;

/** Body of POST /api/auth/login. */
public record LoginRequest(String username, String password) {
}
