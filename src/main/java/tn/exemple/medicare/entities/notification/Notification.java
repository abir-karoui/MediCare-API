package tn.exemple.medicare.entities.notification;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "Notifications")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    private LocalDateTime sentAt;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ManyToOne
    @JsonIgnore

    @JsonBackReference

    private User user;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "prescription_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Prescription prescription;

   // @ManyToOne(fetch = FetchType.LAZY)
   @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "invitation_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Invitation invitation;

}