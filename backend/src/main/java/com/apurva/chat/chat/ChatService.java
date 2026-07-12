package com.apurva.chat.chat;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Business logic for chat, sitting between the controllers and the database.
 *
 * Keeping this logic OUT of the controller follows the Single Responsibility
 * Principle (SOLID): the controller only handles messaging plumbing; the service
 * decides what to save and how to build the response. The repository is injected
 * via the constructor (Dependency Injection), so this class is easy to test.
 */
@Service
public class ChatService {

    private final MessageRepository repository;

    public ChatService(MessageRepository repository) {
        this.repository = repository;
    }

    /** Persist an incoming chat message and return the enriched message to broadcast. */
    public ChatMessage saveChat(ChatMessage incoming) {
        Instant now = Instant.now();
        repository.save(new MessageEntity(incoming.sender(), incoming.content(), now));
        return new ChatMessage(ChatMessage.MessageType.CHAT, incoming.sender(), incoming.content(), now);
    }

    /** Load the most recent messages so a new user sees the conversation so far. */
    public List<ChatMessage> recentHistory() {
        return repository.findTop50ByOrderByTimestampAsc().stream()
                .map(m -> new ChatMessage(
                        ChatMessage.MessageType.CHAT,
                        m.getSender(),
                        m.getContent(),
                        m.getTimestamp()))
                .toList();
    }
}
