package com.apurva.chat.kafka;

/**
 * The event we publish to Kafka when someone sends a message.
 *
 * We use a plain epoch-millisecond timestamp (a long) instead of a Java time
 * type, so it serializes cleanly to/from JSON across Kafka.
 */
public record ChatMessageEvent(
        String sender,
        String recipient,
        String content,
        String attachmentUrl,
        String attachmentType,
        String attachmentName,
        long timestamp) {
}
