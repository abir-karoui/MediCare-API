package tn.exemple.medicare.services;

import org.springframework.data.domain.Page;
import tn.exemple.medicare.entities.ActivityLog;
import tn.exemple.medicare.entities.prescription.Medication;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IActivityLogService {
    void logActivity(String title, String performedBy, String description);

    Page<ActivityLog> getActivities (int pageNo, int pageSize) ;
    Map<String, Long> getLoginStatsLastWeek();
}
