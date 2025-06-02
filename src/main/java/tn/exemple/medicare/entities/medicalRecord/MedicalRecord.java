package tn.exemple.medicare.entities.medicalRecord;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.enums.BloodGroup;

import java.time.LocalDateTime;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "MedicalRecord")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodType;
    private Double height;
    private Double weight;
    @ElementCollection
    private List<String> allergies;
    @ElementCollection
    private List<String> chronicDiseases;
    @OneToOne
    @JoinColumn(name = "patient_id", unique = true)
    @JsonBackReference("patient-medicalRecord")
    @EqualsAndHashCode.Exclude
    private Patient patient;
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference ("operations-medicalRecord")
    private List<Operation> operations;

}
