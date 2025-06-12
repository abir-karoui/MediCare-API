package tn.exemple.medicare.entities.chat;
import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.auth.User;


public class ChatMessageMapper {

    public static ChatMessageDto toDto(ChatMessage message) {
        if (message == null) return null;

        return new ChatMessageDto(
                message.getId(),
                message.getContent(),
                message.getTime(),
                message.isRead(),
                message.getType(),
                toUserMsgDto(message.getSender()),
                toUserMsgDto(message.getReceiver())
        );
    }

    public static ChatMessage toEntity(ChatMessageDto dto) {
        if (dto == null) return null;

        ChatMessage message = new ChatMessage();
        message.setId(dto.getId());
        message.setContent(dto.getContent());
        message.setTime(dto.getTime());
        message.setRead(dto.isRead());
        message.setType(dto.getType());
        message.setSender(toUserEntity(dto.getSender()));
        message.setReceiver(toUserEntity(dto.getReceiver()));
        return message;
    }

    private static UserMsgDto toUserMsgDto(User user) {
        if (user == null) return null;

        return new UserMsgDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole()
        );
    }

    private static User toUserEntity(UserMsgDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }
}
