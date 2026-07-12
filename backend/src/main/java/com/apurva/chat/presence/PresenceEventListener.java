package com.apurva.chat.presence;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks who is online by listening to WebSocket connect/disconnect events,
 * and broadcasts the online-users set to everyone on "/topic/presence".
 */
@Component
public class PresenceEventListener {

    private final SimpMessagingTemplate messaging;
    private final Set<String> online = ConcurrentHashMap.newKeySet();

    public PresenceEventListener(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null && online.add(user.getName())) {
            broadcast();
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null && online.remove(user.getName())) {
            broadcast();
        }
    }

    public Set<String> onlineUsers() {
        return online;
    }

    private void broadcast() {
        messaging.convertAndSend("/topic/presence", online);
    }
}
