package tn.exemple.medicare.entities.prescription;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class MedicationIntakeSimpleDTO {
    private Long id;
    private String medicationName;
    private LocalTime timeToTake;
    private Integer quantity;
}
