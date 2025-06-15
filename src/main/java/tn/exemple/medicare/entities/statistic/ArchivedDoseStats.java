package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ArchivedDoseStats {
    private String timeToTake;  // "08:00"
    private int totalPlanned;
    private int totalTaken;
    private List<LocalDate> intakeDates; // Pour chaque prise réelle
}
