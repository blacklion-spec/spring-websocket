package com.liondog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @author marui
 * @version 1.0.0
 * @ClassName ApplicationWebSocketConfig.java
 * @Description
 * @createTime 2022年07月29日 00:13:00
 */
@Configuration
@EnableWebSocket
public class ApplicationWebSocketConfig implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(),"/websocketTest").addInterceptors(new HttpSessionHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return  new MyHandler();
    }

    public class MyHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            super.afterConnectionEstablished(session);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println(message.getPayload());
        }

        @Override
        public boolean supportsPartialMessages() {
            return super.supportsPartialMessages();
        }
    }

}
