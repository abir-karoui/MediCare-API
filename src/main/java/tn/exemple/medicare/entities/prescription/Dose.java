package tn.exemple.medicare.entities.prescription;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Dose")

public class Dose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime timeToTake;

    private Integer quantity;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    //private boolean notified = false;
}
