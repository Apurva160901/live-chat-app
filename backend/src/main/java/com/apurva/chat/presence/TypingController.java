package com.apurva.chat.presence;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * Relays "typing…" signals privately to the recipient only.
 * Client sends to /app/typing {recipient}; we forward to the recipient's
 * personal queue /user/queue/typing with the sender's name.
 */
@Controller
public class TypingController {

    private final SimpMessagingTemplate messaging;

    public TypingController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/typing")
    public void typing(TypingPayload payload, Principal principal) {
        if (principal == null || payload.recipient() == null) {
            return;
        }
        messaging.convertAndSendToUser(
                payload.recipient(), "/queue/typing", Map.of("sender", principal.getName()));
    }
}
