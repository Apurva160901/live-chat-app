package com.apurva.chat.dm;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * REST endpoint to load a 1:1 conversation's past messages when you open a chat.
 * GET /api/dm/{other} -> messages between the logged-in user and {other}.
 */
@RestController
@RequestMapping("/api/dm")
@CrossOrigin(originPatterns = "*")
public class DmHistoryController {

    private final DirectMessageRepository repo;

    public DmHistoryController(DirectMessageRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{other}")
    public List<DirectMessageDto> history(@PathVariable String other, Principal principal) {
        return repo.conversation(principal.getName(), other).stream()
                .map(d -> new DirectMessageDto(
                        d.getSender(), d.getRecipient(), d.getContent(), d.getTimestamp(),
                        d.getAttachmentUrl(), d.getAttachmentType(), d.getAttachmentName()))
                .toList();
    }
}
