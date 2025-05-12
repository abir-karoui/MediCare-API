package tn.exemple.medicare.entities.notification;


import lombok.*;
import tn.exemple.medicare.enums.NotificationType;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationResponse {
   /* private int status;
    private String message;*/
    private Long id;
    private String title;
    private String body;
    private LocalDateTime sentAt;
    private NotificationType type;
    private Long idInvitation;
}
