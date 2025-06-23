package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ArchivedMedicationStatsResponse {
    private String medicationName;
    private int plannedDosesCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ArchivedDoseStats> doses;
}

