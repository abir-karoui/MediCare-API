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
import tn.exemple.medicare.repositories.IDoseRepository;
import tn.exemple.medicare.repositories.NotificationRepository;
import tn.exemple.medicare.services.impl.FCMService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class MedicationReminderScheduler {
    private final  IDoseRepository doseRepository;
    private final FCMService fcmService;
    private  final NotificationRepository notificationRepository;
    private Logger logger = LoggerFactory.getLogger(FCMService.class);

    @Scheduled(cron = "0 * * * * *")
    public void checkDoses() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalTime oneMinuteLater = now.plusMinutes(1); //hedhi bch difference des secondes ne provoque pas des err lors de notif
        doseRepository.findUnnotifiedDosesNearNow(now,oneMinuteLater).forEach(dose -> {
            if (dose.getPrescription().isActive()) {
                sendDoseNotification(dose);
                dose.setNotified(true);
                doseRepository.save(dose);
            }
        });

    }
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à 00:00
    @Transactional
    public void resetNotifiedDoses() {
        //System.out.println("resetNotifiedDoses execute a : " + LocalTime.now());
        doseRepository.resetAllNotifiedDoses();
    }

    public void sendDoseNotification( Dose dose) {
        User patient = dose.getPrescription().getUser();
        String body = String.format("It's time to take %s at %s, with a dosage of %s.",
                dose.getPrescription().getMedication().getDenomination(),
                dose.getTimeToTake().format(DateTimeFormatter.ofPattern("HH:mm")),
                dose.getQuantity());

        NotificationRequest request = new NotificationRequest();
        request.setTitle("Medication Recall");
        request.setBody(body);
        request.setToken(patient.getFcmToken());

        try {
            fcmService.sendMessageToToken(request);
            Notification entity = Notification.builder()
                    .title("Medication Recall")
                    .body(body)
                    .sentAt(LocalDateTime.now())
                    .user(patient)
                    .prescription(dose.getPrescription())
                    .build();

            notificationRepository.save(entity);
        } catch (Exception e) {
            logger.error("Échec d'envoi de notification pour la dose ID: " + dose.getId(), e);
        }

    }
}
