package tn.exemple.medicare.entities.auth;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.TypeGender;
import tn.exemple.medicare.enums.TypeRole;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

public  class User implements Serializable , UserDetails , Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @Column(unique = true)
    @Email( message = "Email is not formated")
    @NotEmpty( message = "Email is not formated")
    @NotBlank(message = "Email is not formated")
    private String email;
    @NotEmpty
    @Size(min =8 , message = "Password should be 8 characters long minimum")
    private String password;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String address;
    private String photo;

    @Enumerated(EnumType.STRING)
    private TypeRole role;
    @Enumerated(EnumType.STRING)

    private TypeGender gender ;

    @JsonIgnore
    private boolean accountLocked;

    private boolean enabled ;
    @JsonIgnore

    private String fcmToken;
    @Override
    public boolean isEnabled() {
        return enabled;
    }




    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastModifieDate() {
        return lastModifieDate;
    }

    public void setLastModifieDate(LocalDateTime lastModifieDate) {
        this.lastModifieDate = lastModifieDate;
    }

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @CreatedDate
    @Column(insertable = false)
    private LocalDateTime lastModifieDate;

    @Override
    public String getName() {
        return email;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    public String fullName(){
        return  firstname + " " + lastname;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private  List<Codes> codes;

    @JsonIgnore
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private  List<RefreshToken> refreshTokens;


   /* @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions;*/



    @JsonIgnore
    @JsonManagedReference("user-notification")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Invitation> sentInvitations;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Invitation> receivedInvitations ;

}
