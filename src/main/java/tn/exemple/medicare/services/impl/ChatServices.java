package tn.exemple.medicare.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.services.IChatServices;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServices implements IChatServices {

    private final ChatMessageRepository chatMessageRepository;
    @Override

    public ChatMessage saveMsg(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
    @Override
    public List<ChatMessage> getChatBetweenUsers(User user1, User user2) {
        return chatMessageRepository.findBySenderAndReceiverOrReceiverAndSender(user1, user2, user2, user1);
    }
    @Override
    public List<ChatMessage> getAllMessagesForUser(User user) {
        return chatMessageRepository.findBySenderOrReceiver(user, user);
    }

    @Override
    public List<ChatMessage> getUnreadMessages(User user) {
        return chatMessageRepository.findByReceiverAndReadFalse(user);
    }
    @Override
    public void markAsRead(ChatMessage message) {
        message.setRead(true);
        chatMessageRepository.save(message);
    }



}
