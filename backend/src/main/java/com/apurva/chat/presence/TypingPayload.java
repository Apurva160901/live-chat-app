package com.apurva.chat.presence;

/** Sent by a client over WebSocket to say "I'm typing to this recipient". */
public record TypingPayload(String recipient) {
}
