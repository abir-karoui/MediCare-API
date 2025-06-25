package tn.exemple.medicare.entities.statistic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.entities.dto.UserDto;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDashboardDTO {
    private int totalPatients;
    private int totalPrescriptions;
    private List<UserDto> recentPatients;
    private Map<String, Integer> prescriptionsPerDay;
}
