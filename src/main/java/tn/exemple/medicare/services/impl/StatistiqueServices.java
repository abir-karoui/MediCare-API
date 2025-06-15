package tn.exemple.medicare.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.prescription.*;
import tn.exemple.medicare.entities.statistic.*;
import tn.exemple.medicare.repositories.IDoseRepository;
import tn.exemple.medicare.repositories.IMedicationIntakeRepository;
import tn.exemple.medicare.repositories.IMedicationRepository;
import tn.exemple.medicare.repositories.IPrescriptionRepository;
import tn.exemple.medicare.services.IStatistiqueServices;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueServices implements IStatistiqueServices {

    private final IPrescriptionRepository prescriptionRepository;
    private final IDoseRepository iDoseRepository;
    private final MedicationServices medicationServices;
    private final IMedicationRepository iMedicationRepository;
    private  final AuthService authService;
    private  final IMedicationIntakeRepository medicationIntakeRepository;
    @Override
    public List<MedicationStatsResponse> getAllMedicationStatsForCurrentUser() {
        Long patientId = authService.getAuthenticatedUserId();

        List<Prescription> prescriptions = prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        List<MedicationStatsResponse> statsList = new ArrayList<>();

        for (Prescription prescription : prescriptions) {
            if (!prescription.isActive()) {
                continue;
            }

            String medicationName = prescription.getMedication().getDenomination();
            int durationDays = (int) ChronoUnit.DAYS.between(
                    prescription.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;

            List<Dose> doses = prescription.getDoses();
            List<MedicationIntake> intakes = prescription.getMedicationIntakes();

            List<DoseStats> doseStatsList = new ArrayList<>();

            for (Dose dose : doses) {
                LocalTime time = dose.getTimeToTake();

                int totalPlanned = durationDays;
                int totalTaken = (int) intakes.stream()
                        .filter(i -> i.getTimeToTake().equals(time) && i.isTaken())
                        .count();

                doseStatsList.add(new DoseStats(time.toString(), totalPlanned, totalTaken));
            }

            int totalPlannedDoses = durationDays * doses.size();

            statsList.add(new MedicationStatsResponse(
                    medicationName,
                    durationDays,
                    doses.size(),
                    totalPlannedDoses,
                    doseStatsList
            ));
        }

        return statsList;
    }
    @Override
    public List<ArchivedMedicationStatsResponse> getArchivedMedicationStatsForCurrentUser() {
        Long patientId = authService.getAuthenticatedUserId();

        List<Prescription> prescriptions = prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        List<ArchivedMedicationStatsResponse> archivedList = new ArrayList<>();

        for (Prescription prescription : prescriptions) {
            if (prescription.isActive()) continue; // ne traiter que les prescriptions inactives

            String medicationName = prescription.getMedication().getDenomination();
            List<Dose> doses = prescription.getDoses();
            List<MedicationIntake> intakes = prescription.getMedicationIntakes();

            // ✅ Calcul des dates de début et de fin
            LocalDate startDate = prescription.getCreatedAt().toLocalDate();
            LocalDate endDate = startDate.plusDays(prescription.getDurationDays());

            long durationDays = ChronoUnit.DAYS.between(startDate, endDate);

            List<ArchivedDoseStats> doseStatsList = new ArrayList<>();
            int totalPlanned = 0;
            int totalTaken = 0;

            for (Dose dose : doses) {
                LocalTime time = dose.getTimeToTake();

                int planned = (int) durationDays; // dose prévue une fois par jour pour chaque heure
                int taken = (int) intakes.stream()
                        .filter(i -> i.getTimeToTake().equals(time) && i.isTaken())
                        .count();

                // ✅ Liste des dates manquées (taken = false)
                List<LocalDate> missedDates = intakes.stream()
                        .filter(i -> i.getTimeToTake().equals(time)
                                && !i.isTaken()
                                && !i.getDate().isAfter(LocalDate.now()))
                        .map(MedicationIntake::getDate)
                        .distinct()
                        .sorted()
                        .toList();

                doseStatsList.add(new ArchivedDoseStats(
                        time.toString(),
                        planned,
                        taken,
                        missedDates // anciennement "intakeDates"
                ));

                totalPlanned += planned;
                totalTaken += taken;
            }

            archivedList.add(new ArchivedMedicationStatsResponse(
                    medicationName,
                    totalPlanned,
                    totalTaken,
                    startDate,
                    endDate,
                    doseStatsList
            ));
        }

        return archivedList;
    }
    @Override

    public TodayPatientSummary getTodaySummary() {
        Long patientId = authService.getAuthenticatedUserId();
        LocalDate today = LocalDate.now();

        List<MedicationIntake> todayIntakes = medicationIntakeRepository
                .findByPatientIdAndDate(patientId, today);

        // Médicaments uniques
        long uniqueMedicationsCount = todayIntakes.stream()
                .map(MedicationIntake::getMedicationName)
                .distinct()
                .count();

        int totalDosesToday = todayIntakes.size();
        int takenDosesCount = (int) todayIntakes.stream().filter(MedicationIntake::isTaken).count();

        List<PlannedDoseDto> plannedDoses = todayIntakes.stream()
                .sorted(Comparator.comparing(MedicationIntake::getTimeToTake))
                .map(intake -> new PlannedDoseDto(
                        intake.getTimeToTake(),
                        intake.getMedicationName(),
                        intake.getQuantity(),
                        intake.isTaken()
                ))
                .collect(Collectors.toList());

        Optional<PlannedDoseDto> nextDose = plannedDoses.stream()
                .filter(dose -> !dose.isTaken() && dose.getTime().isAfter(LocalTime.now()))
                .findFirst();

        return new TodayPatientSummary(
                today,
                uniqueMedicationsCount,
                totalDosesToday,
                takenDosesCount,
                nextDose.orElse(null),
                plannedDoses
        );
    }



}
