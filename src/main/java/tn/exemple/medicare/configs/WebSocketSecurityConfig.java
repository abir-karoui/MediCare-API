package tn.exemple.medicare.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;


@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {



    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages

                .nullDestMatcher().permitAll()
                .simpTypeMatchers(
                        org.springframework.messaging.simp.SimpMessageType.CONNECT,
                        org.springframework.messaging.simp.SimpMessageType.DISCONNECT,
                        org.springframework.messaging.simp.SimpMessageType.OTHER
                ).permitAll()
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").authenticated()

                .anyMessage().authenticated();
    }
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}