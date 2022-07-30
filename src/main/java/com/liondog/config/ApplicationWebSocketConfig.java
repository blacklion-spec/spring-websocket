package com.liondog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
public class ApplicationWebSocketConfig implements WebSocketConfigurer {

    @Bean
    public ServletServerContainerFactoryBean createWevSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(5000L); //会话空闲超时是直接关闭连接
        return container;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //HttpSessionHandshakeInterceptor的作用是将HttpSession中的属性传递给attributes（Map）中，包括sessionId，还有放在session中的属性
        registry.addHandler(myHandler(), "/webSocketTest").addInterceptors(new HttpSessionHandshakeInterceptor(),myHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new MyHandler();
    }

    @Bean
    public HandshakeInterceptor myHandshakeInterceptor() {
        return new MyHandshakeInterceptor();
    }

    public class MyHandler extends TextWebSocketHandler {

        /**
         * 处理心跳数据
         * 服务端主动发送PingMessage，客户端回复了PongMessage后，会回调此方法
         */
        @Override
        protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
            log.info("接受到心跳回复 : " + message);
        }

        //在WebSocket协商成功并且WebSocket连接打开并准备使用之后调用。
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.debug("连接可用 ： " + session.getId());
        }

        //处理文本类消息
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println(message.getPayload());
            session.sendMessage(new TextMessage("12121"));
            session.sendMessage(new PingMessage());

        }

        /**
         * WebSocketHandler是否处理部分消息。
         * 如果该标志设置为true并且底层的WebSocket服务器支持部分消息，
         * 那么一个大的WebSocket消息，或者一个未知大小的消息可能会被分割，
         * 并可能通过多次调用handleMessage(WebSocketSession, WebSocketMessage)来接收。
         * 标志WebSocketMessage.isLast()指示消息是否是部分的，是否为最后一部分。
         */
        @Override
        public boolean supportsPartialMessages() {
            return false;
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            session.close(CloseStatus.SERVER_ERROR);
            log.error("websocket通信异常：" + exception.getMessage());
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.debug("连接关闭：" + status.getCode() + "原因：" + status.getReason());
        }
    }

    public class MyHandshakeInterceptor implements HandshakeInterceptor {

        /*
            在处理握手之前调用。
            参数: 请求-当前请求 响应-当前响应 目标WebSocket处理程序 attribute—HTTP握手中与WebSocket会话相关联的属性;复制提供的属性，不使用原始映射。
            返回: 是继续握手(true)还是中止握手(false)
         */
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            log.debug("握手之前");
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            log.debug("握手之后");
        }
    }

}
