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
import tn.exemple.medicare.services.IStatistiqueServices;

import java.util.List;


@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatistiquesContollers {
    private final IStatistiqueServices iStatistiqueServices;

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

}
