package tn.exemple.medicare.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.enums.TypeRole;

import java.util.Set;



@Entity
@AllArgsConstructor
@Data
@Table(name = "Patient")
public class Patient extends User {
    String age ;
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
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

