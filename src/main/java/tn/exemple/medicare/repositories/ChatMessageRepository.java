package tn.exemple.medicare.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.entities.chat.Discussion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderOrReceiver(User sender, User receiver);

    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(
            User sender1, User receiver1, User sender2, User receiver2
    );
    List<ChatMessage> findByReceiverAndReadFalse(User receiver);
    Page<ChatMessage> findByDiscussionId(Long discussionId, Pageable pageable);
    Page<ChatMessage> findByDiscussionIdAndTimeAfter(Long discussionId, LocalDateTime time, Pageable pageable);
   /* @Query("""
    SELECT COUNT(DISTINCT m.discussion.id)
    FROM ChatMessage m
    WHERE m.receiver.id = :userId
      AND m.read = false
""")
    int countUnreadDiscussionsForUser(@Param("userId") Long userId);*/
   @Query("""
    SELECT DISTINCT m.discussion.id
    FROM ChatMessage m
    WHERE m.receiver.id = :userId
      AND m.read = false
""")
   List<Long> findUnreadDiscussionIdsForUser(@Param("userId") Long userId);





}
