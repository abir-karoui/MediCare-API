package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PlannedDoseDto {
    private LocalTime time;
    private String medicationName;
    private int quantity;
    private boolean taken;
}