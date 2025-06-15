package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MedicationStatsResponse {
    private String medicationName;
    private int durationDays;
    private int dailyDosesCount;
    private int totalPlannedDoses;
    private List<DoseStats> doses;
}
