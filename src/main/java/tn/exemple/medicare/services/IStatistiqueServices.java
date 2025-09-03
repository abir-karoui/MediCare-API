package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.statistic.ArchivedMedicationStatsResponse;
import tn.exemple.medicare.entities.statistic.DoctorDashboardDTO;
import tn.exemple.medicare.entities.statistic.MedicationStatsResponse;
import tn.exemple.medicare.entities.statistic.TodayPatientSummary;

import java.util.List;
import java.util.Map;

public interface IStatistiqueServices {

    List<MedicationStatsResponse> getAllMedicationStatsForCurrentUser();
    List<ArchivedMedicationStatsResponse> getArchivedMedicationStatsForCurrentUser();
    TodayPatientSummary getTodaySummary();
    public DoctorDashboardDTO getDoctorDashboard();
    Map<String, Integer> getTopDoctors();
    Map<String, Integer> getTop3ChronicDiseases();
}
