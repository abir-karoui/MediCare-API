package tn.exemple.medicare.entities.auth;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId" , nullable = false)
    private User user;

}
