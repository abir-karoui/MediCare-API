package tn.exemple.medicare.entities.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private LocalDateTime time;
    private boolean read = false;
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "sendermsg_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receivermsg_id")
    private User receiver;
    @PrePersist
    protected void onCreate() {
        this.time = LocalDateTime.now();
    }



}
