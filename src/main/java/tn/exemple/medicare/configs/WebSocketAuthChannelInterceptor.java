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
    /*
    private final JwtService jwtService;
    private final UserDetailsServices userDetailsServices;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, UserDetailsServices userDetailsServices) {
        this.jwtService = jwtService;
        this.userDetailsServices = userDetailsServices;
        System.out.println("🔧 WebSocketAuthChannelInterceptor créé");
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("📩 INTERCEPTEUR DÉCLENCHÉ!");
        System.out.println("📩 Message: " + message);
        System.out.println("📩 Channel: " + channel);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            System.out.println("🔍 Type de commande STOMP: " + accessor.getCommand());
            System.out.println("🔍 Headers STOMP: " + accessor.toNativeHeaderMap());

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                System.out.println("🔐 Tentative d'authentification STOMP CONNECT");
                authenticateUser(accessor);
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            } else {
                System.out.println("📝 Autre commande STOMP: " + accessor.getCommand());
            }
        } else {
            System.out.println("⚠️ Pas d'accessor STOMP trouvé");
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        System.out.println("📤 PostSend appelé - Message envoyé: " + sent);
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        System.out.println("✅ AfterSendCompletion - Success: " + sent + ", Exception: " + ex);
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user != null) {
            String username = user.getName();
            System.out.println("👋 Utilisateur déconnecté: " + username);
        }
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            System.out.println("🔐 Début authentification WebSocket");



            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null) {
                token = accessor.getFirstNativeHeader("authorization");
            }

            System.out.println("🎫 Token trouvé: " + (token != null ? "OUI" : "NON"));

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("🎫 Token après nettoyage: " + token.substring(0, Math.min(20, token.length())) + "...");

                String username = jwtService.extractUsername(token);
                System.out.println("👤 Username extrait du JWT: " + username);

                if (username != null) {
                    UserDetails userDetails = userDetailsServices.loadUserByUsername(username);
                    System.out.println("👤 UserDetails chargé: " + userDetails.getUsername());

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Définir l'utilisateur dans STOMP
                        accessor.setUser(auth);

                        // Définir dans le contexte de sécurité
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        System.out.println("✅ AUTHENTIFICATION WEBSOCKET RÉUSSIE pour: " + username);
                        System.out.println("✅ Authorities: " + userDetails.getAuthorities());
                    } else {
                        System.err.println("❌ Token JWT invalide pour: " + username);
                        throw new IllegalArgumentException("Token JWT invalide");
                    }
                } else {
                    System.err.println("❌ Impossible d'extraire le username du JWT");
                    throw new IllegalArgumentException("JWT malformé");
                }
            } else {
                System.err.println("❌ Token manquant ou format incorrect");
                System.err.println("Token reçu: " + token);
                throw new IllegalArgumentException("Token Bearer manquant");
            }
        } catch (Exception e) {
            System.err.println("💥 ERREUR D'AUTHENTIFICATION WebSocket: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Authentification WebSocket échouée: " + e.getMessage());
        }
    }*/
}