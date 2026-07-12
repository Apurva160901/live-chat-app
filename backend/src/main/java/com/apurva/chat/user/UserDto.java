package com.apurva.chat.user;

/** Public view of a user (no password) sent to the frontend. */
public record UserDto(String username, String displayName, String avatarUrl) {
}
