package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ArchivedDoseStats {
    private String timeToTake;
    private int totalTaken;
}
