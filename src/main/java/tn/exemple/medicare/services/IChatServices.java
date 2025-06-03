package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;

import java.util.List;

public interface IChatServices {
     ChatMessage saveMsg(ChatMessage message) ;

     List<ChatMessage> getChatBetweenUsers(User user1, User user2) ;

     List<ChatMessage> getAllMessagesForUser(User user) ;

     List<ChatMessage> getUnreadMessages(User user) ;
    void markAsRead(ChatMessage message);
}
