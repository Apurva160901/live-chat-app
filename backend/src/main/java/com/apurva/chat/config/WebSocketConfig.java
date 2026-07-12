package com.apurva.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket + STOMP configuration.
 *
 * WebSocket gives us a permanent two-way connection between browser and server.
 * STOMP is a simple messaging protocol on top of it (like "envelopes" with a
 * destination address), so we can do pub/sub: clients subscribe to a topic and
 * the server broadcasts messages to everyone subscribed.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Destinations starting with /topic are handled by an in-memory broker
        // that BROADCASTS each message to all subscribed clients (pub/sub).
        registry.enableSimpleBroker("/topic");

        // Destinations starting with /app are routed to our @MessageMapping methods
        // (i.e. messages the client SENDS to the server for processing).
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The browser opens the WebSocket connection at this URL.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // dev only: allow the React dev server origin
                .withSockJS();                 // fallback if the browser/network blocks raw WebSocket
    }
}
