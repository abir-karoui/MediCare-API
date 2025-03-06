package tn.exemple.medicare.entities.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private long id ;
    private  String resetToken ;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}