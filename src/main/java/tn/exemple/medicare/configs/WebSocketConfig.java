package tn.exemple.medicare.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    private final WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;
    public WebSocketConfig(
                           WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor) {

        this.webSocketAuthChannelInterceptor = webSocketAuthChannelInterceptor;
    }

   /* public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor,
                           WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
        this.webSocketAuthChannelInterceptor = webSocketAuthChannelInterceptor;
    }*/

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") ;
               // .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthChannelInterceptor);
    }
}
