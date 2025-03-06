package tn.exemple.medicare.entities.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.exemple.medicare.enums.TypeCode;
import tn.exemple.medicare.enums.TypeRole;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class Codes {
    @Id
    @GeneratedValue
    private long id ;
    private String code;
    @Enumerated(EnumType.STRING)
    private TypeCode typecode;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime validateAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId" , nullable = false)
    private User user;
}
