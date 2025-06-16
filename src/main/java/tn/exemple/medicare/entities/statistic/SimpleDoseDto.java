package tn.exemple.medicare.entities.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SimpleDoseDto {
    private LocalTime time;
    private int quantity;
    private boolean taken;
}
