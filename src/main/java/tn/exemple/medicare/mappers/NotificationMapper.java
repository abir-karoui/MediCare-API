package tn.exemple.medicare.mappers;

import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationResponse;

@Component
public class NotificationMapper {
    public NotificationResponse mapToDto(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .sentAt(notification.getSentAt())
                .type(notification.getType())
                .idInvitation(notification.getInvitation() != null ? notification.getInvitation().getId() : null)
                .idMedicationIntake(notification.getMedicationIntake() != null ? notification.getMedicationIntake().getId() : null)
                .build();
    }
}
