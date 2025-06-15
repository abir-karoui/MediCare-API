package tn.exemple.medicare.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.entities.chat.ChatMessageDto;
import tn.exemple.medicare.entities.chat.Discussion;
import tn.exemple.medicare.entities.chat.DiscussionResponseDTO;

import java.util.List;

public interface IChatServices {
    Page<ChatMessageDto> getMessagesByDiscussion(Long discussionId, Pageable pageable);
    Page<DiscussionResponseDTO> getUserDiscussions(Pageable pageable);
    Page<ChatMessageDto> getMessagesWithUser(Long otherUserId, Pageable pageable);

    //List<ChatMessage> getUnreadMessages(User user) ;
    void markAsRead(ChatMessage message);
    void deleteDiscussionForUser(Long discussionId);
    void deleteMessage(Long messageId);
   // int getUnreadDiscussionsCount();
   List<Long> getUnreadDiscussionIds();
}
