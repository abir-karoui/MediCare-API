package tn.exemple.medicare.entities.notification;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import tn.exemple.medicare.enums.NotificationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationRequest{
    private String title;
    private String body;
    private String topic;
    private String token;
    /*@Enumerated(EnumType.STRING)
    private NotificationType type;
    private Long idNotification;*/
}
