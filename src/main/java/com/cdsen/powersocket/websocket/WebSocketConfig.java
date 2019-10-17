package com.cdsen.powersocket.websocket;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.module.connection.message.ConnectionError;
import com.cdsen.security.util.JwtParseUtils;
import com.cdsen.user.UserLoginInfo;
import com.cdsen.user.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;
import java.util.Objects;

/**
 * 直接用Redis存储用户登录信息 需要一个基于Redis的用户信息登录模块
 *
 * @author HuSen
 * create on 2019/10/11 10:41
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UserManager userManager;

    public WebSocketConfig(UserManager userManager) {
        this.userManager = userManager;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 表示设置消息代理的前缀，即如果消息的前缀是”/topic”，就会将消息转发给代理（broker）,再由消息代理将消息广播给当前连接的客户端
        registry.enableSimpleBroker("/queue/", "/topic/");
        // 表示配置一个或多个前缀，通过这些前缀过滤出需要被注解方法处理的消息
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 表示定义一个前缀为”/pushServer”的edPoint,并开启sockjs支持，sockjs可以解决浏览器对WebSocket的兼容性问题，客户端将通过这里配置的URL来建立WebSocket连接
        registry.addEndpoint("/pushServer").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        ChannelInterceptor interceptor = new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (Objects.nonNull(accessor) && Objects.nonNull(accessor.getCommand())) {
                    String authorization = ConfigUtils.getProperty(AppProperties.Security.HEADER, "authorization");
                    List<String> nativeHeader = accessor.getNativeHeader(authorization);

                    if (CollectionUtils.isEmpty(nativeHeader)) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }

                    String token = nativeHeader.get(0);
                    if (StringUtils.isBlank(token)) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }

                    String secret = ConfigUtils.getProperty(AppProperties.Security.SECRET, "");
                    String username = JwtParseUtils.getUsernameFromToken(secret, token);
                    if (StringUtils.isBlank(username)) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }

                    UserLoginInfo loginInfo = userManager.getLoginInfo(token);
                    if (Objects.isNull(loginInfo)) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }

                    if (!StringUtils.equals(username, loginInfo.getUsername())) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.INFO_NOT_MATCH));
                    }

                    if (!loginInfo.isEnabled()) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }

                    if (!loginInfo.isAccountNonLocked()) {
                        return new GenericMessage<>(MessageResult.of(ConnectionError.CAN_NOT_ACCESS));
                    }
                }
                return message;
            }
        };

        registration.interceptors(interceptor);
    }
}
