package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;

import java.util.List;

@Repository

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderOrReceiver(User sender, User receiver);

    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(
            User sender1, User receiver1, User sender2, User receiver2
    );
    List<ChatMessage> findByReceiverAndReadFalse(User receiver);
}
