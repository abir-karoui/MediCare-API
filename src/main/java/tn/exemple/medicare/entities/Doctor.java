package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;



@AllArgsConstructor
@Entity
@DiscriminatorValue("Doctor")

public class Doctor extends User {
    String spatiality ;


    public String getSpatiality() {
        return spatiality;
    }

    public void setSpatiality(String spatiality) {
        this.spatiality = spatiality;
    }

    public  Doctor (){
        this.setRole(TypeRole.DOCTOR);
    }

    @ManyToMany(mappedBy = "doctors")
    Set<Patient> patients;


}


