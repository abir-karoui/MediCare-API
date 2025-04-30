package tn.exemple.medicare.entities.notification;


import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationResponse {
    private int status;
    private String message;
}
