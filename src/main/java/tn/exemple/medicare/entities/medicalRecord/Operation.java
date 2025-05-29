package tn.exemple.medicare.entities.medicalRecord;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate dateOperation;
    private String surgeon;
    private String hospital;
    private String description;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;




}
