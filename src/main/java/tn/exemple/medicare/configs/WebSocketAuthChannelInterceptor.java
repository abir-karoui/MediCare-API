package tn.exemple.medicare.configs;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsServices userDetailsServices;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, UserDetailsServices userDetailsServices) {
        this.jwtService = jwtService;
        this.userDetailsServices = userDetailsServices;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
     System.out.println("📩 Intercepteur déclenché : " + message);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            System.out.println("Type de message STOMP: " + accessor.getCommand());

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                authenticateUser(accessor);
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            }
        }

        return message;
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user != null) {
            String username = user.getName();
            System.out.println("Utilisateur déconnecté : " + username);
        }
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            String token = accessor.getFirstNativeHeader("Authorization");

            System.out.println("Token reçu: " + (token != null ? "Présent" : "Absent"));

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                String username = jwtService.extractUsername(token);
                System.out.println("Username extrait: " + username);

                UserDetails userDetails = userDetailsServices.loadUserByUsername(username);
                System.out.println("UserDetails chargé: " + userDetails.getUsername());

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    accessor.setUser(auth);
                    System.out.println("✅ Authentification WebSocket réussie pour: " + username);
                } else {
                    System.err.println("❌ Token invalide pour: " + username);
                    throw new IllegalArgumentException("Token invalide");
                }
            } else {
                System.err.println("❌ Token manquant ou format incorrect");
                throw new IllegalArgumentException("Token manquant ou mal formé");
            }
        } catch (Exception e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
            e.printStackTrace();
            throw e;  // Rejette la connexion STOMP
        }
    }
}