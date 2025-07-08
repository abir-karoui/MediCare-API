package tn.exemple.medicare.entities.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {
    private String email;
    private String role;
    private String userJson;
    private String photoUrl;
    private String medicalCardUrl;
    private String encodedPassword;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

