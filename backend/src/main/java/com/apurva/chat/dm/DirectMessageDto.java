package com.apurva.chat.dm;

import java.time.Instant;

/** A direct message as sent to clients (and returned in history), incl. optional attachment. */
public record DirectMessageDto(
        String sender,
        String recipient,
        String content,
        Instant timestamp,
        String attachmentUrl,
        String attachmentType,
        String attachmentName) {
}
