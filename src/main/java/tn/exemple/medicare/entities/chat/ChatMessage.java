package tn.exemple.medicare.entities.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tn.exemple.medicare.entities.auth.User;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    //private String photoUrl;
    private LocalDateTime time;
    private boolean read = false;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "sendermsg_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receivermsg_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;
    @PrePersist
    protected void onCreate() {
        this.time = LocalDateTime.now();
    }



}
