package tn.exemple.medicare.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue
    private long id ;
    private  String refreshToken ;
    private Instant createdAt;
    private Instant expiredAt;
    @ManyToOne
    @JoinColumn(name = "userId" , nullable = false)
    private  User user;

}
