// com.chess.config.WebSocketConfig.java

package com.chess.backend.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket URL
                .setAllowedOriginPatterns("*")
                .withSockJS();      // fallback for older clients
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // for broadcasting messages
        config.setApplicationDestinationPrefixes("/app"); // client -> server
    }
}
