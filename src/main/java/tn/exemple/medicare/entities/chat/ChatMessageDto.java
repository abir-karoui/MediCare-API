package tn.exemple.medicare.entities.chat;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.entities.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String content;
    private LocalDateTime time;
    private boolean read;
    private MessageType type;

    private UserMsgDto sender;
    private UserMsgDto receiver;
}