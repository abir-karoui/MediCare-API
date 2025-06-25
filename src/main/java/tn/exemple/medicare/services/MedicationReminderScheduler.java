package tn.exemple.medicare.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationRequest;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.NotificationType;
import tn.exemple.medicare.repositories.IDoseRepository;
import tn.exemple.medicare.repositories.IMedicationIntakeRepository;
import tn.exemple.medicare.repositories.IPrescriptionRepository;
import tn.exemple.medicare.repositories.NotificationRepository;
import tn.exemple.medicare.services.impl.FCMService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MedicationReminderScheduler {

   private final IMedicationIntakeRepository medicationIntakeRepository;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;
    private final IPrescriptionRepository prescriptionRepository;

    private final Logger logger = LoggerFactory.getLogger(MedicationReminderScheduler.class);
    @Transactional
    @Scheduled(cron = "0 35 15 * * *")
    public void generateTodayMedicationIntakes() {
        LocalDate today = LocalDate.now();

        List<Prescription> activePrescriptions = prescriptionRepository.findAll();

        for (Prescription prescription : activePrescriptions) {
            if (prescription.isActive()) {
                for (Dose dose : prescription.getDoses()) {
                    MedicationIntake intake = MedicationIntake.builder()
                            .date(today)
                            .timeToTake(dose.getTimeToTake())
                            .quantity(dose.getQuantity())
                            .medicationName(prescription.getMedication().getDenomination())
                            .prescription(prescription)
                            .patient(prescription.getPatient())
                            .taken(false)
                            .notified(false)
                            .build();

                    medicationIntakeRepository.save(intake);
                }
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkMedicationIntakes() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        List<MedicationIntake> intakes = medicationIntakeRepository
                .findByDateAndTimeToTakeAndNotifiedFalse(today, now);

        for (MedicationIntake intake : intakes) {
            if (intake.getPrescription().isActive()) {
                sendMedicationIntakeNotification(intake);
                intake.setNotified(true);
                medicationIntakeRepository.save(intake);
            }
        }
    }

    public void sendMedicationIntakeNotification(MedicationIntake intake) {
        User patient = intake.getPatient();

        String body = String.format("It's time to take %s at %s, dosage: %s.",
                intake.getMedicationName(),
                intake.getTimeToTake().format(DateTimeFormatter.ofPattern("HH:mm")),
                intake.getQuantity());

        NotificationRequest request = new NotificationRequest();
        request.setTitle("Medication Reminder");
        request.setBody(body);
        request.setToken(patient.getFcmToken());

        try {
            fcmService.sendMessageToToken(request);

            Notification notification = Notification.builder()
                    .title("Medication Reminder")
                    .body(body)
                    .sentAt(LocalDateTime.now())
                    .user(patient)
                    .type(NotificationType.REMINDER)
                    .medicationIntake(intake)
                    .build();

            notificationRepository.save(notification);
        } catch (Exception e) {
            logger.error("Failed to send notification for intake ID: " + intake.getId(), e);
        }
    }
}
