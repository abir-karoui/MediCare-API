package tn.exemple.medicare.entities.prescription;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Prescription")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    Integer  durationDays;
    Integer stockActuel;
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Dose> doses;
    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @ManyToOne
    @JsonBackReference("patient-prescription")
    @JoinColumn(name = "Patient_id")
    //private User user;
    private Patient patient;
    @ManyToOne
    @JsonBackReference("doctor-prescription")
    @JoinColumn(name = "Doctor_id")
    private Doctor doctor;

    @JsonIgnore
    @JsonManagedReference("medicationintake")
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationIntake> medicationIntakes;



    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    public boolean isActive() {
        return ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDate.now()) <= durationDays;
    }




}
