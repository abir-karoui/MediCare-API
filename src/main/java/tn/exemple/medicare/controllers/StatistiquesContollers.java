package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.entities.statistic.ArchivedMedicationStatsResponse;
import tn.exemple.medicare.entities.statistic.DoctorDashboardDTO;
import tn.exemple.medicare.entities.statistic.MedicationStatsResponse;
import tn.exemple.medicare.entities.statistic.TodayPatientSummary;
import tn.exemple.medicare.services.IActivityLogService;
import tn.exemple.medicare.services.IStatistiqueServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatistiquesContollers {
    private final IStatistiqueServices iStatistiqueServices;
    private final IActivityLogService activityLogService;

    @GetMapping("/prescriptions")
    public ResponseEntity<List<MedicationStatsResponse>> getStatsForCurrentUser() {
        return ResponseEntity.ok(iStatistiqueServices.getAllMedicationStatsForCurrentUser());
    }
    @GetMapping("/prescriptions/archived")
    public ResponseEntity<List<ArchivedMedicationStatsResponse>> getArchivedStatsForCurrentUser() {
        return ResponseEntity.ok(iStatistiqueServices.getArchivedMedicationStatsForCurrentUser());
    }
    @GetMapping("/dashboard/today")
    public ResponseEntity<TodayPatientSummary> getTodayDashboard() {
        TodayPatientSummary summary = iStatistiqueServices.getTodaySummary();
        return ResponseEntity.ok(summary);
    }
    @GetMapping("/dashboard/doctor")
    public ResponseEntity<DoctorDashboardDTO> getDashboard() {
        DoctorDashboardDTO dashboard = iStatistiqueServices.getDoctorDashboard();
        return ResponseEntity.ok(dashboard);
    }
    @GetMapping("/login/last-week")
    public ResponseEntity<Map<String, Long>> getLoginStatsLastWeek() {
        Map<String, Long> stats = activityLogService.getLoginStatsLastWeek();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-doctors")
    public Map<String, Integer> getTopDoctors() {
        return iStatistiqueServices.getTopDoctors();
    }

    @GetMapping("/top-chronicdiseases")
    public Map<String, Integer> getTop3ChronicDiseases() {
        return iStatistiqueServices.getTop3ChronicDiseases();
    }
}
