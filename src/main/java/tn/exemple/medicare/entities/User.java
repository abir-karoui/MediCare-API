package tn.exemple.medicare.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Entity(name="user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="usertab", discriminatorType = DiscriminatorType.STRING)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String firstname;
    String lastname;
    String email;
    String mdp;
    String phone;
    String address;
    String photo;
    @Enumerated(EnumType.STRING)
    TypeRole role;
    @Enumerated(EnumType.STRING)
    TypeGender gender ;

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

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
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






}
