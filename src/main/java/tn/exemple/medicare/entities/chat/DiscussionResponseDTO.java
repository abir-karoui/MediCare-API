package tn.exemple.medicare.entities.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DiscussionResponseDTO {
    private Long discussionId;
    private Long otherUserId;
    private String otherUserFullName;
    private String otherUserEmail;
    private String otherUserProfileImage;
    private String lastMessage;
    private String lastMessageTime;
}