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
    long id;
    @JsonFormat(pattern = "HH:mm")
    LocalTime timeToTake;

     String  quantity;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "prescription_id")

    private Prescription prescription;

}
