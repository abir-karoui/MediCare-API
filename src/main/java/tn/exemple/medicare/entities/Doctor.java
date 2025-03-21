package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;

import java.util.Set;




@Getter
@Setter
@Entity
@Data
@Table(name = "Doctor")
@AllArgsConstructor

public class Doctor extends User {

    String specialty ;


    public  Doctor (){
        this.setRole(TypeRole.DOCTOR);
    }

    @ManyToMany(mappedBy = "doctors")
    Set<Patient> patients;


}


