package tn.exemple.medicare.entities.auth;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "role")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Doctor.class, name = "DOCTOR"),
        @JsonSubTypes.Type(value = Patient.class, name = "PATIENT")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

public  class User implements Serializable , UserDetails , Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String address;

    private String photo;
    @Enumerated(EnumType.STRING)
    private TypeRole role;
    @Enumerated(EnumType.STRING)
    private TypeGender gender ;

    @JsonIgnore
    private boolean accountLocked;
    @JsonIgnore
    private boolean enabled ;


    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
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
    @OneToMany(mappedBy = "user")
    private  List<Codes> codes;


    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private  List<RefreshToken> refreshTokens;
}
