package tn.exemple.medicare.controllers.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.entities.chat.ChatMessageDto;
import tn.exemple.medicare.entities.chat.ChatMessageMapper;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.repositories.IUserRepository;

import java.security.Principal;
import java.util.Objects;



//ce fichier point d'entree STOMP
@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private  IUserRepository iUserRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.register")
    @SendTo("/topic/public") //si je veux message pour user bien defini  @SendTo("/topic/{id}")
    public ChatMessage register(@Payload ChatMessage chatMessage , SimpMessageHeaderAccessor headerAccessor){
        Objects.requireNonNull(  headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender().getEmail());
        return chatMessage;

    }
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        return chatMessage;

    }

    /*@MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessage message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver().getEmail(), "/queue/messages", message);// envoyer a un user precis ( /user/receiverId/queue/messages)
    }*/
    /*
    @MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessage message, Principal principal) {
        // Vérifier que l'utilisateur authentifié correspond à l'expéditeur
        if (principal != null && message.getSender() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver().getEmail(),
                    "/queue/messages",
                    message
            );
        }
    }*/
    @MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessageDto messageDto, Principal principal) {
        if (principal != null) {
            String senderEmail = principal.getName();

            User sender = iUserRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String receiverEmail = messageDto.getReceiver() != null ? messageDto.getReceiver().getEmail() : null;
            System.out.println("Receiver email: " + receiverEmail);

            User receiver = iUserRepository.findByEmail(receiverEmail)
                    .orElseThrow(() -> new RuntimeException("Destinataire non trouvé par email"));

            ChatMessage message = ChatMessageMapper.toEntity(messageDto);
            message.setSender(sender);
            message.setReceiver(receiver);
            chatMessageRepository.save(message);
            ChatMessageDto responseDto = ChatMessageMapper.toDto(message);

            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    responseDto
            );
        }
    }





}
