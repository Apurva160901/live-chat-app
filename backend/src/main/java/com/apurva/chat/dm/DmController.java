package com.apurva.chat.dm;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

/**
 * Handles private (1:1) messages over WebSocket, including optional attachments.
 * Sender comes from the authenticated WebSocket principal; the message is saved
 * and delivered to both the recipient and the sender via per-user queues.
 */
@Controller
public class DmController {

    private final DirectMessageRepository repo;
    private final SimpMessagingTemplate messaging;

    public DmController(DirectMessageRepository repo, SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.messaging = messaging;
    }

    @MessageMapping("/dm.send")
    public void send(DmPayload payload, Principal principal) {
        if (principal == null || payload.recipient() == null) {
            return;
        }
        boolean hasContent = payload.content() != null && !payload.content().isBlank();
        boolean hasAttachment = payload.attachmentUrl() != null && !payload.attachmentUrl().isBlank();
        if (!hasContent && !hasAttachment) {
            return;
        }

        String sender = principal.getName();
        Instant now = Instant.now();
        String content = payload.content() == null ? "" : payload.content();

        DirectMessage entity = new DirectMessage(sender, payload.recipient(), content, now);
        entity.setAttachmentUrl(payload.attachmentUrl());
        entity.setAttachmentType(payload.attachmentType());
        entity.setAttachmentName(payload.attachmentName());
        repo.save(entity);

        DirectMessageDto dto = new DirectMessageDto(sender, payload.recipient(), content, now,
                payload.attachmentUrl(), payload.attachmentType(), payload.attachmentName());
        messaging.convertAndSendToUser(payload.recipient(), "/queue/messages", dto);
        messaging.convertAndSendToUser(sender, "/queue/messages", dto);
    }
}
