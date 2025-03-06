package tn.exemple.medicare.entities.auth;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
/*public class Token {
    @Id
    @GeneratedValue
    private long id ;
    private  String token ;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime validateAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId" , nullable = false)
    private User user;

}*/
