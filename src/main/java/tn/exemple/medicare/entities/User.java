package tn.exemple.medicare.entities;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.exemple.medicare.enums.TypeGender;
import tn.exemple.medicare.enums.TypeRole;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDate;
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

public  class User implements Serializable , UserDetails , Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
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
        return false;
    }
    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
*/
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public TypeRole getRole() {
        return role;
    }

    public void setRole(TypeRole role) {
        this.role = role;
    }

    public TypeGender getGender() {
        return gender;
    }
    public void setGender(TypeGender gender) {
        this.gender = gender;
    }
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
    public boolean isEnabled() {
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
}
