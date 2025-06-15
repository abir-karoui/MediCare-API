package tn.exemple.medicare.services.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.*;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.repositories.IDiscussionReposiroty;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.services.IChatServices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServices implements IChatServices {

    private final ChatMessageRepository chatMessageRepository;

    private  final IDiscussionReposiroty discussionRepository;
    private  final AuthService authService;
    private final IUserRepository userRepository;


    @Override
    public Page<ChatMessageDto> getMessagesByDiscussion(Long discussionId, Pageable pageable) {
        if (!discussionRepository.existsById(discussionId)) {
            throw new EntityNotFoundException("Discussion non trouvée");
        }

        Page<ChatMessage> messagesPage = chatMessageRepository.findByDiscussionId(discussionId, pageable);

        return messagesPage.map(ChatMessageMapper::toDto);
    }
   /* @Override
    public Page<ChatMessageDto> getMessagesWithUser(Long otherUserId, Pageable pageable) {
        Long currentUserId = authService.getAuthenticatedUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur actuel non trouvé"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("Autre utilisateur non trouvé"));

        Discussion discussion = discussionRepository
                .findByUsers(currentUser, otherUser)
                .orElseThrow(() -> new EntityNotFoundException("Discussion entre utilisateurs non trouvée"));

        Page<ChatMessage> messagesPage = chatMessageRepository.findByDiscussionId(discussion.getId(), pageable);


        List<ChatMessage> unreadMessages = messagesPage.getContent().stream()
                .filter(msg -> msg.getSender().getId() == otherUserId && !msg.isRead())
                .toList();

        unreadMessages.forEach(this::markAsRead);

        return messagesPage.map(ChatMessageMapper::toDto);
    }*/
   @Override
   public Page<ChatMessageDto> getMessagesWithUser(Long otherUserId, Pageable pageable) {
       Long currentUserId = authService.getAuthenticatedUserId();

       User currentUser = userRepository.findById(currentUserId)
               .orElseThrow(() -> new EntityNotFoundException("Utilisateur actuel non trouvé"));

       User otherUser = userRepository.findById(otherUserId)
               .orElseThrow(() -> new EntityNotFoundException("Autre utilisateur non trouvé"));

       Discussion discussion = discussionRepository
               .findByUsers(currentUser, otherUser)
               .orElseThrow(() -> new EntityNotFoundException("Discussion entre utilisateurs non trouvée"));


       LocalDateTime deletedAt = null;
       if (discussion.getUser1().getId() ==(currentUserId)) {
           deletedAt = discussion.getDeletedAtByUser1();
       } else if (discussion.getUser2().getId() == (currentUserId)) {
           deletedAt = discussion.getDeletedAtByUser2();
       }

       Page<ChatMessage> messagesPage;
       if (deletedAt != null) {
           messagesPage = chatMessageRepository.findByDiscussionIdAndTimeAfter(
                   discussion.getId(), deletedAt, pageable);
       } else {
           messagesPage = chatMessageRepository.findByDiscussionId(
                   discussion.getId(), pageable);
       }


       List<ChatMessage> unreadMessages = messagesPage.getContent().stream()
               .filter(msg -> msg.getSender().getId() == (otherUserId) && !msg.isRead())
               .toList();

       unreadMessages.forEach(this::markAsRead);

       return messagesPage.map(ChatMessageMapper::toDto);
   }




    @Override

   /* public Page<DiscussionResponseDTO> getUserDiscussions(Pageable pageable) {
        Long userId = authService.getAuthenticatedUserId();
        Page<Discussion> discussions = discussionRepository
                .findByUser1IdOrUser2IdOrderByLastMessageTimeDesc(userId, userId, pageable);

        return discussions.map(discussion -> {
            User otherUser = discussion.getUser1().getId() == userId
                    ? discussion.getUser2()
                    : discussion.getUser1();


            return new DiscussionResponseDTO(
                    discussion.getId(),
                    otherUser.getId(),
                    otherUser.getFirstname() + " " + otherUser.getLastname(),
                    otherUser.getEmail(),
                    otherUser.getPhoto(),
                    discussion.getLastMessage(),
                    discussion.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            );
        });
    }*/

    public Page<DiscussionResponseDTO> getUserDiscussions(Pageable pageable) {
        Long userId = authService.getAuthenticatedUserId();
        Page<Discussion> discussions = discussionRepository
                .findVisibleDiscussionsForUser(userId, pageable);

        return discussions.map(discussion -> {
            User otherUser = discussion.getUser1().getId() == (userId)
                    ? discussion.getUser2()
                    : discussion.getUser1();

            return new DiscussionResponseDTO(
                    discussion.getId(),
                    otherUser.getId(),
                    otherUser.getFirstname() + " " + otherUser.getLastname(),
                    otherUser.getEmail(),
                    otherUser.getPhoto(),
                    discussion.getChatMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getTime))
                            .map(ChatMessage::getContent)
                            .orElse(null),

                    //discussion.getLastMessage(),
                    discussion.getLastMessageTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
        });
    }



   /* @Override
    public List<ChatMessage> getUnreadMessages(User user) {
        return chatMessageRepository.findByReceiverAndReadFalse(user);
    }*/
    @Override
    public void markAsRead(ChatMessage message) {
        message.setRead(true);
        chatMessageRepository.save(message);
    }
    @Override

    public void deleteDiscussionForUser(Long discussionId) {
        Long userId = authService.getAuthenticatedUserId();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found"));

        if (discussion.getUser1().getId()== userId) {
            discussion.setDeletedAtByUser1(LocalDateTime.now());
        } else if (discussion.getUser2().getId()== userId) {
            discussion.setDeletedAtByUser2(LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("User not part of this discussion");
        }

        discussionRepository.save(discussion);
    }

    @Override
    public void deleteMessage(Long messageId) {
        Long currentUserId = authService.getAuthenticatedUserId();

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message non trouvé"));

        if (message.getSender().getId() != (currentUserId)) {
            throw new SecurityException("Vous ne pouvez supprimer que vos propres messages");
        }

        chatMessageRepository.delete(message);
    }


    @Override
    /*public int getUnreadDiscussionsCount() {
        Long currentUserId = authService.getAuthenticatedUserId();
        return chatMessageRepository.countUnreadDiscussionsForUser(currentUserId);
    }*/
    public List<Long> getUnreadDiscussionIds() {
        Long currentUserId = authService.getAuthenticatedUserId();
        return chatMessageRepository.findUnreadDiscussionIdsForUser(currentUserId);
    }






}
