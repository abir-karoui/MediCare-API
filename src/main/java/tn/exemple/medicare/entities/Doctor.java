package tn.exemple.medicare.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.TypeRole;

import java.util.List;
import java.util.Set;




@Getter
@Setter
@Entity
@Data
@Table(name = "Doctor")
@AllArgsConstructor

public class Doctor extends User {

    private String speciality ;
    private String medicalCard;
    private boolean medicalCardVerified;
    public  Doctor (){
        this.setRole(TypeRole.DOCTOR);
    }

    @ManyToMany(mappedBy = "doctors")
    @JsonIgnore
    Set<Patient> patients;
    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions;


}


