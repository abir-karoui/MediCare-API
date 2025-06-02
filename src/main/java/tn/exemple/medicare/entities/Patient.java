package tn.exemple.medicare.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.medicalRecord.MedicalRecord;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.TypeRole;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Entity
@Data
@Table(name = "Patient")
public class Patient extends User {

    private String age ;
    public Patient(){
            this.setRole(TypeRole.PATIENT);
        }
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "patient_doctor",
            joinColumns = @JoinColumn(name = "idPatient"),
            inverseJoinColumns = @JoinColumn(name = "idDoctor"))
    Set<Doctor> doctors;
    @JsonIgnore

    @ManyToMany
    @JoinTable(
            name = "patient_diseases",
            joinColumns = @JoinColumn(name = "idPatient"),
            inverseJoinColumns = @JoinColumn(name = "idDiseases"))
    Set<Diseases> diseases ;

    @JsonIgnore
    @JsonManagedReference("patient-prescription")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("patient-medicationIntakes")
    private List<MedicationIntake> medicationIntakes;

   @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("patient-medicalRecord")
   @EqualsAndHashCode.Exclude
    private MedicalRecord medicalRecord;
}

