package tn.exemple.medicare.controllers.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.entities.chat.ChatMessageDto;
import tn.exemple.medicare.entities.chat.ChatMessageMapper;
import tn.exemple.medicare.entities.chat.Discussion;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.repositories.IDiscussionReposiroty;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IChatServices;
import tn.exemple.medicare.services.IDiseases;

import java.security.Principal;
import java.time.LocalDateTime;
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
    @Autowired
    private IDiscussionReposiroty discussionReposiroty;


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
    @MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessageDto messageDto, Principal principal) {
        if (principal != null) {
            String senderEmail = principal.getName();

            User sender = iUserRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur (expéditeur) non trouvé"));

            String receiverEmail = messageDto.getReceiver() != null ? messageDto.getReceiver().getEmail() : null;
            if (receiverEmail == null) {
                throw new RuntimeException("Email du destinataire manquant");
            }

            User receiver = iUserRepository.findByEmail(receiverEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur (destinataire) non trouvé"));

            Discussion discussion = discussionReposiroty.findByUsers(sender, receiver)
                    .orElseGet(() -> {
                        Discussion newDiscussion = new Discussion();
                        newDiscussion.setUser1(sender);
                        newDiscussion.setUser2(receiver);
                        return discussionReposiroty.save(newDiscussion);
                    });

            ChatMessage message = ChatMessageMapper.toEntity(messageDto);
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setDiscussion(discussion);
            message.setTime(LocalDateTime.now());
            discussion.setLastMessageTime(message.getTime());
            discussion.setLastMessage(message.getContent());
            discussionReposiroty.save(discussion);
            ChatMessage savedMessage = chatMessageRepository.save(message);

            ChatMessageDto responseDto = ChatMessageMapper.toDto(savedMessage);

            try {
                messagingTemplate.convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/messages",
                        responseDto
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
            }
        }
    }




   /* @MessageMapping("/chat.private")
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

            ChatMessage savedMessage = chatMessageRepository.save(message);

            ChatMessageDto responseDto = ChatMessageMapper.toDto(savedMessage);

            try {
                messagingTemplate.convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/messages",
                        responseDto
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du message: " + e.getMessage());
            }
        }
    }*/

    /*@MessageMapping("/chat.private")
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
    }*/
  /* @MessageMapping("/chat.register")
   @SendTo("/topic/public")
   public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
       // Récupérer l'utilisateur authentifié
       Principal user = headerAccessor.getUser();
       System.out.println("Utilisateur connecté: " + (user != null ? user.getName() : "ANONYME"));

       if (user != null) {
           headerAccessor.getSessionAttributes().put("username", user.getName());
       }

       return chatMessage;
   }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal user) {
        System.out.println("Message envoyé par: " + (user != null ? user.getName() : "ANONYME"));
        return chatMessage;
    }

*/


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



}
