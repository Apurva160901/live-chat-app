package com.apurva.chat.auth;

/** Body of POST /api/auth/forgot-password. */
public record ForgotPasswordRequest(String username) {
}
