package tn.exemple.medicare.entities.prescription;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Entity
@Data
@Table(name = "Medications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Medication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String denomination ;

    @OneToMany(mappedBy = "medication")
    @JsonBackReference("medication-prescription")
    private List<Prescription> prescriptions;


}

