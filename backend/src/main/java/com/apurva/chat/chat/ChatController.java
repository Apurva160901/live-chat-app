package com.apurva.chat.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

/**
 * Handles real-time chat messages arriving over WebSocket/STOMP.
 *
 * Flow:
 *   1. A browser SENDS a message to "/app/chat.send".
 *   2. Spring routes it to the matching @MessageMapping method below.
 *   3. The method delegates to ChatService (save + build response).
 *   4. Whatever the method returns is BROADCAST (@SendTo) to "/topic/public".
 *   5. Every browser subscribed to "/topic/public" instantly receives it.
 */
@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /** A user sends a chat message: save it, then broadcast it to everyone. */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage send(ChatMessage incoming) {
        return chatService.saveChat(incoming);
    }

    /** A user joins the room. We announce it to everyone (not persisted). */
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessage join(ChatMessage incoming) {
        return new ChatMessage(
                ChatMessage.MessageType.JOIN,
                incoming.sender(),
                incoming.sender() + " joined the chat",
                Instant.now());
    }
}
