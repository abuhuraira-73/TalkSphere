package com.chatapp.chatappbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefix for messages that are bound for methods annotated with @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        
        // Set prefix for the endpoint that the client will subscribe to
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set prefix for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint, enabling Spring's STOMP support
        registry.addEndpoint("/ws")
            .setAllowedOrigins("*") // For development, restrict in production
            .withSockJS(); // Enable SockJS fallback options
    }
} 