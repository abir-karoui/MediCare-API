package tn.exemple.medicare.entities.prescription;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.entities.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "medication_intake")
public class MedicationIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime timeToTake;

    private Integer quantity;

    private boolean taken = false;

    private String medicationName;

    private boolean notified = false;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    @JsonBackReference
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;
}