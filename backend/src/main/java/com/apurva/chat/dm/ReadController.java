package com.apurva.chat.dm;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;

/**
 * Read receipts. When I read a conversation, I send /app/dm.read {recipient}.
 * We notify that person on /user/queue/read that I (the reader) have seen their
 * messages up to now — their UI can then show ✓✓ (seen).
 */
@Controller
public class ReadController {

    private final SimpMessagingTemplate messaging;

    public ReadController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/dm.read")
    public void read(ReadPayload payload, Principal principal) {
        if (principal == null || payload.recipient() == null) {
            return;
        }
        messaging.convertAndSendToUser(
                payload.recipient(), "/queue/read",
                Map.of("reader", principal.getName(), "at", Instant.now().toString()));
    }
}
