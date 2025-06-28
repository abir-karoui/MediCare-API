package tn.exemple.medicare.mappers;

import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationResponse;

@Component
public class NotificationMapper {
    public NotificationResponse mapToDto(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setBody(notification.getBody());
        response.setSentAt(notification.getSentAt());
        response.setType(notification.getType());

        if (notification.getInvitation() != null) {
            response.setIdInvitation(notification.getInvitation().getId());
            response.setInvitationStatus(notification.getInvitation().getStatus()); // Important
        }

        if (notification.getMedicationIntake() != null) {
            response.setIdMedicationIntake(notification.getMedicationIntake().getId());
        }

        return response;
    }

}
