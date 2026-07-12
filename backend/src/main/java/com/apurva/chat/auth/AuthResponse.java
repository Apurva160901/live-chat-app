package com.apurva.chat.auth;

/** What we return after a successful register/login: the token + basic profile. */
public record AuthResponse(String token, String username, String displayName, String avatarUrl) {
}
