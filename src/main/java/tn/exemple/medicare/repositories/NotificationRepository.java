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
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.NotificationType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByInvitation(Invitation invitation);
    @Modifying
    @Query("UPDATE Notification n SET n.invitation = NULL WHERE n.invitation.id = :invitationId")
    void nullifyInvitationReferences(@Param("invitationId") Long invitationId);
    @Modifying
    @Query("UPDATE Notification n SET n.prescription = NULL WHERE n.prescription.id = :prescriptionId")
    void nullifyPrescriptionReferences(@Param("prescriptionId") Long prescriptionId);

    int countByUserIdAndReadFalse(Long userId);
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.read = false")
    void markAllAsReadForUser(@Param("userId") Long userId);
    void deleteBySentAtBefore(LocalDateTime dateTime);
    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);






}
