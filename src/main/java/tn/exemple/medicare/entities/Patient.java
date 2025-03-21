package tn.exemple.medicare.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;
import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@Entity
@AllArgsConstructor
@Data
@Table(name = "Patient")
public class Patient extends User {

    private String age ;
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

