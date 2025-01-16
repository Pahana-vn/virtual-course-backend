package com.mytech.virtualcourse.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Định nghĩa endpoint WebSocket mà frontend sẽ kết nối
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
        // /ws-chat là endpoint STOMP
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // prefix mà client gửi tin nhắn lên server
        config.setApplicationDestinationPrefixes("/app");
        // prefix mà server gửi message về client
        config.enableSimpleBroker("/topic", "/queue");
    }
}
