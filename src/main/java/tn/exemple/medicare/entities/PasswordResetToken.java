package tn.exemple.medicare.entities;

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


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}