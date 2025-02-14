package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("Doctor")
public class Doctor extends User {
    String specialite ;

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    @ManyToMany(mappedBy = "doctors")
    Set<Patient> patients;
    /*public  Doctor (){
        this.setRole(TypeRole.DOCTOR);
    }*/

}
