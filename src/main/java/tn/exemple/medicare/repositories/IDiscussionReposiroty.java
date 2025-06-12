package tn.exemple.medicare.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.chat.ChatMessage;
import tn.exemple.medicare.entities.chat.Discussion;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDiscussionReposiroty  extends JpaRepository<Discussion, Long> {
    @Query("SELECT d FROM Discussion d WHERE " +
            "(d.user1 = :user1 AND d.user2 = :user2) OR " +
            "(d.user1 = :user2 AND d.user2 = :user1)")
    Optional<Discussion> findByUsers(User user1, User user2);
    Page<Discussion> findByUser1IdOrUser2IdOrderByLastMessageTimeDesc(Long userId1, Long userId2, Pageable pageable);



}
