package tn.exemple.medicare.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.dto.UserDto;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.prescription.*;
import tn.exemple.medicare.entities.statistic.*;
import tn.exemple.medicare.mappers.UserMapper;
import tn.exemple.medicare.repositories.*;
import tn.exemple.medicare.services.IStatistiqueServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueServices implements IStatistiqueServices {

    private final IPrescriptionRepository prescriptionRepository;
    private  final AuthService authService;
    private  final IMedicationIntakeRepository medicationIntakeRepository;
    private  final InvitationRepository invitationRepository;
    private  final InvitationServices invitationServices;

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
            if (prescription.isActive()) continue;

            String medicationName = prescription.getMedication().getDenomination();
            List<Dose> doses = prescription.getDoses();
            List<MedicationIntake> intakes = prescription.getMedicationIntakes();

            LocalDate startDate = prescription.getCreatedAt().toLocalDate();
            LocalDate endDate = startDate.plusDays(prescription.getDurationDays());


            List<ArchivedDoseStats> doseStatsList = new ArrayList<>();
            int plannedDosesCount = prescription.getDurationDays();

            for (Dose dose : doses) {
                LocalTime time = dose.getTimeToTake();
                int taken = (int) intakes.stream()
                        .filter(i -> i.getTimeToTake().equals(time) && i.isTaken())
                        .count();


                doseStatsList.add(new ArchivedDoseStats(
                        time.toString(),
                        taken
                ));
            }

            archivedList.add(new ArchivedMedicationStatsResponse(
                    medicationName,
                    plannedDosesCount,
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

        long uniqueMedicationsCount = todayIntakes.stream()
                .map(MedicationIntake::getMedicationName)
                .distinct()
                .count();

        int totalDosesToday = todayIntakes.size();
        int takenDosesCount = (int) todayIntakes.stream()
                .filter(MedicationIntake::isTaken)
                .count();

        // ✅ Calcul de la prochaine dose
        Optional<PlannedDoseDto> nextDose = todayIntakes.stream()
                .filter(intake -> !intake.isTaken() && intake.getTimeToTake().isAfter(LocalTime.now()))
                .sorted(Comparator.comparing(MedicationIntake::getTimeToTake))
                .map(intake -> new PlannedDoseDto(
                        intake.getTimeToTake(),
                        intake.getMedicationName(),
                        intake.getQuantity(),
                        intake.isTaken()
                ))
                .findFirst();

        // ✅ Regrouper par nom de médicament
        Map<String, List<SimpleDoseDto>> groupedDoses = todayIntakes.stream()
                .sorted(Comparator.comparing(MedicationIntake::getTimeToTake))
                .collect(Collectors.groupingBy(
                        MedicationIntake::getMedicationName,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                intake -> new SimpleDoseDto(
                                        intake.getTimeToTake(),
                                        intake.getQuantity(),
                                        intake.isTaken()
                                ),
                                Collectors.toList()
                        )
                ));

        return new TodayPatientSummary(
                today,
                uniqueMedicationsCount,
                totalDosesToday,
                takenDosesCount,
                nextDose.orElse(null),
                groupedDoses
        );
    }



@Override
    public DoctorDashboardDTO getDoctorDashboard() {
        Long currentUserId = authService.getAuthenticatedUserId();

        // 1. Nombre de patients amis
        List<UserDto> allConnectedUsers = invitationServices.getConnectedUsers();
        int totalPatients = allConnectedUsers.size();

        // 2. Nombre total de prescriptions du docteur
        int totalPrescriptions = prescriptionRepository.countByDoctorId(currentUserId);

        // 3. Les 5 derniers patients (amis) selon date de création d'invitation
        List<Invitation> invitations = invitationRepository.findAcceptedInvitationsByUser(currentUserId);
        List<UserDto> recentPatients = invitations.stream()
                .sorted(Comparator.comparing(Invitation::getCreatedAt).reversed())
                .map(inv -> {
                    if (inv.getSender().getId() == (currentUserId)) {
                        return UserMapper.toDto(inv.getReceiver());
                    } else {
                        return UserMapper.toDto(inv.getSender());
                    }
                })
                .distinct()
                .limit(5)
                .toList();


    /*Map<String, Integer> prescriptionsPerDay = new LinkedHashMap<>();
    Locale locale = Locale.ENGLISH; // ou Locale.ENGLISH

// 1. Ajouter les 7 derniers jours de AUJOURD’HUI → il y a 6 jours
    for (int i = 0; i < 7; i++) {
        LocalDate day = LocalDate.now().minusDays(i); // aujourd’hui, -1, -2, ...
        String dayName = day.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        prescriptionsPerDay.put(dayName, 0);
    }

// 2. Remplir les vraies valeurs retournées par la BDD
    List<Object[]> stats = prescriptionRepository.countByDoctorIdGroupedByDayLast7Days(currentUserId);

    for (Object[] row : stats) {
        java.sql.Date sqlDate = (java.sql.Date) row[0];
        Integer count = ((Number) row[1]).intValue();

        LocalDate date = sqlDate.toLocalDate();
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

        // Si le jour existe dans la map, on met à jour
        if (prescriptionsPerDay.containsKey(dayName)) {
            prescriptionsPerDay.put(dayName, count);
        }
    }
*/

    Map<String, Integer> prescriptionsPerDay = new LinkedHashMap<>();
    Locale locale = Locale.ENGLISH;
    List<LocalDate> last7Days = new ArrayList<>();
    for (int i = 6; i >= 0; i--) {
        last7Days.add(LocalDate.now().minusDays(i));
    }

    for (LocalDate day : last7Days) {
        String dayName = day.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        prescriptionsPerDay.put(dayName, 0);
    }

// 2. Remplir avec les vraies données de la BDD
    List<Object[]> stats = prescriptionRepository.countByDoctorIdGroupedByDayLast7Days(currentUserId);

    for (Object[] row : stats) {
        java.sql.Date sqlDate = (java.sql.Date) row[0];
        Integer count = ((Number) row[1]).intValue();

        LocalDate date = sqlDate.toLocalDate();
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

        // Mettre à jour uniquement si le jour est dans la map
        if (prescriptionsPerDay.containsKey(dayName)) {
            prescriptionsPerDay.put(dayName, count);
        }
    }
        DoctorDashboardDTO dto = new DoctorDashboardDTO();
        dto.setTotalPatients(totalPatients);
        dto.setTotalPrescriptions(totalPrescriptions);
        dto.setRecentPatients(recentPatients);
        dto.setPrescriptionsPerDay(prescriptionsPerDay);
        return dto;
    }




}
