package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TodayPatientSummary {
    private LocalDate date;
    private long uniqueMedicationsCount;
    private int totalDosesToday;
    private int takenDosesCount;
    private PlannedDoseDto nextDose;
    private List<PlannedDoseDto> plannedDoses;
}