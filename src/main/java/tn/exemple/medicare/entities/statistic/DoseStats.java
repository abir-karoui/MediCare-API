package tn.exemple.medicare.entities.statistic;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoseStats {
    private String timeToTake; // "08:00"
    private int totalPlanned;  // ex: 5
    private int totalTaken;    // ex: 4
}