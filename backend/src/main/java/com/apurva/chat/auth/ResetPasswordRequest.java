package com.apurva.chat.auth;

/** Body of POST /api/auth/reset-password. */
public record ResetPasswordRequest(String token, String newPassword) {
}
