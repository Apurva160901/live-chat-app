package com.apurva.chat.dm;

/**
 * Sent when I open/read a conversation. `recipient` here is the ORIGINAL sender
 * whose messages I just read — we notify them that their messages were seen.
 */
public record ReadPayload(String recipient) {
}
