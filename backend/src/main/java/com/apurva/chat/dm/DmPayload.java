package com.apurva.chat.dm;

/**
 * What the client sends over WebSocket to deliver a direct message.
 * content and/or the attachment fields may be set.
 */
public record DmPayload(
        String recipient,
        String content,
        String attachmentUrl,
        String attachmentType,
        String attachmentName) {
}
