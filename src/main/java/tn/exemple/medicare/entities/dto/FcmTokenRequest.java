package tn.exemple.medicare.entities.dto;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FcmTokenRequest{
private Long userId;
private String fcmToken;

}