package com.apurva.chat.chat;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A plain REST endpoint so the React app can load past messages when it opens.
 *
 * GET /api/messages  ->  returns the recent chat history as JSON.
 *
 * (@RestController = @Controller + @ResponseBody: every method's return value is
 * automatically converted to JSON.)
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(originPatterns = "*") // dev only: let the React dev server call this API
public class ChatHistoryController {

    private final ChatService chatService;

    public ChatHistoryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<ChatMessage> history() {
        return chatService.recentHistory();
    }
}
