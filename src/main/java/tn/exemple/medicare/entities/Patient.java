package tn.exemple.medicare.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;

import java.time.LocalDate;
import java.util.Set;



@Entity
@AllArgsConstructor
@Data
@Table(name = "Patient")
public class Patient extends User {
     private LocalDate dateOfBirth ;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Patient(){
            this.setRole(TypeRole.PATIENT);
        }
    @ManyToMany
    @JoinTable(
            name = "patient_doctor",
            joinColumns = @JoinColumn(name = "idPatient"),
            inverseJoinColumns = @JoinColumn(name = "idDoctor"))
    Set<Doctor> doctors;

    @ManyToMany
    @JoinTable(
            name = "patient_diseases",
            joinColumns = @JoinColumn(name = "idPatient"),
            inverseJoinColumns = @JoinColumn(name = "idDiseases"))
    Set<Diseases> diseases ;
}

