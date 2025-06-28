package tn.exemple.medicare.entities.chat;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.invitation.Invitation;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user2;

    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime deletedAtByUser1;
    private LocalDateTime deletedAtByUser2;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages ;


}

