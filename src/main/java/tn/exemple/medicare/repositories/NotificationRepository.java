package tn.exemple.medicare.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByInvitation(Invitation invitation);
    @Modifying
    @Query("UPDATE Notification n SET n.invitation = NULL WHERE n.invitation.id = :invitationId")
    void nullifyInvitationReferences(@Param("invitationId") Long invitationId);




}
