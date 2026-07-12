package com.apurva.chat.presence;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Lets a client fetch the current set of online users when it first loads
 * (before it starts receiving live "/topic/presence" updates).
 */
@RestController
@RequestMapping("/api/presence")
@CrossOrigin(originPatterns = "*")
public class PresenceController {

    private final PresenceEventListener presence;

    public PresenceController(PresenceEventListener presence) {
        this.presence = presence;
    }

    @GetMapping
    public Set<String> online() {
        return presence.onlineUsers();
    }
}
