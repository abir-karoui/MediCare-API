package tn.exemple.medicare.entities.invitation;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private TypeRole senderType;

    @Enumerated(EnumType.STRING)
    private TypeRole receiverType;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status ;

    private LocalDateTime createdAt = LocalDateTime.now();

}
