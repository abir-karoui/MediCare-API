package tn.exemple.medicare.entities.notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationRequest {
    private String title;
    private String body;
    private String topic;
    private String token;
}
