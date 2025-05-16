package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.exemple.medicare.entities.invitation.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository  extends JpaRepository<Invitation, Long> {
    @Query("SELECT i FROM Invitation i WHERE (i.sender.id = :senderId AND i.receiver.id = :receiverId OR i.sender.id = :receiverId AND i.receiver.id = :senderId) AND i.status = 'PENDING'")
    Optional<Invitation> findPendingInvitation(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    @Query("SELECT i FROM Invitation i WHERE (i.sender.id = :user1 AND i.receiver.id = :user2) OR (i.sender.id = :user2 AND i.receiver.id = :user1) ORDER BY i.createdAt DESC LIMIT 1")
    Optional<Invitation> findLatestBetweenUsers(Long user1, Long user2);
    @Query("SELECT i FROM Invitation i WHERE i.status = 'ACCEPTED' AND (i.sender.id = :userId OR i.receiver.id = :userId)")
    List<Invitation> findAcceptedInvitationsByUser(@Param("userId") Long userId);


}
