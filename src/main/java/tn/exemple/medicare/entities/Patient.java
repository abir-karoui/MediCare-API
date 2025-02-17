package tn.exemple.medicare.entities;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;



@Entity
@DiscriminatorValue("Patient")
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

