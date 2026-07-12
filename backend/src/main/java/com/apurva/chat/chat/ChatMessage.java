package com.apurva.chat.chat;

import java.time.Instant;

/**
 * A single chat message that travels between browser and server as JSON.
 *
 * This is a Java "record" — a compact, immutable data carrier. Jackson (Spring's
 * JSON library) automatically converts the JSON <-> this record.
 *
 * Example JSON the client sends:  {"type":"CHAT","sender":"apurva","content":"hi"}
 */
public record ChatMessage(
        MessageType type,
        String sender,
        String content,
        Instant timestamp
) {
    /** What kind of event this message represents. */
    public enum MessageType {
        CHAT,   // a normal chat message
        JOIN,   // someone joined the room
        LEAVE   // someone left the room
    }
}
