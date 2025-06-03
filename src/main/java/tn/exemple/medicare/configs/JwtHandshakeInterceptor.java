package tn.exemple.medicare.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        System.out.println("Début du handshake WebSocket");

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            // Récupérer le token depuis les paramètres URL
            String token = httpServletRequest.getParameter("token");
            if (token != null && !token.isEmpty()) {
                attributes.put("token", token);
                System.out.println("🎫 Token reçu dans le handshake");
            }

            // Log des headers pour debug
            System.out.println("📋 Headers de la requête:");
            httpServletRequest.getHeaderNames().asIterator()
                    .forEachRemaining(name ->
                            System.out.println("  " + name + ": " + httpServletRequest.getHeader(name)));
        }

        return true; // Toujours autoriser le handshake
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

        if (exception != null) {
            System.err.println("Erreur lors du handshake: " + exception.getMessage());
        } else {
            System.out.println("Handshake WebSocket terminé avec succès");
        }
    }
}