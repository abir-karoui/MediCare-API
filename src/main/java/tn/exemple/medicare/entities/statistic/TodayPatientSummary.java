package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TodayPatientSummary {
    private LocalDate date;
    private long uniqueMedicationsCount;
    private int totalDosesToday;
    private int takenDosesCount;
    private PlannedDoseDto nextDose;
    private Map<String, List<SimpleDoseDto>> plannedDosesGrouped;
}