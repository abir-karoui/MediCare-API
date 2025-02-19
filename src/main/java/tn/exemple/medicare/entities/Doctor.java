package tn.exemple.medicare.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.exemple.medicare.enums.TypeRole;

import java.util.Set;




@Entity
@Data
@Table(name = "Doctor")
@AllArgsConstructor

public class Doctor extends User {
    String specialtiy ;


    public String getSpecialtiy() {
        return specialtiy;
    }

    public void setSpecialtiy(String specialtiy) {
        this.specialtiy = specialtiy;
    }

    public  Doctor (){
        this.setRole(TypeRole.DOCTOR);
    }

    @ManyToMany(mappedBy = "doctors")
    Set<Patient> patients;


}


