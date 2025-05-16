package tn.exemple.medicare.services.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.FcmTokenRequest;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationRequest;
import tn.exemple.medicare.entities.notification.NotificationResponse;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.NotificationType;
import tn.exemple.medicare.mappers.NotificationMapper;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.repositories.NotificationRepository;
import tn.exemple.medicare.services.INotificationServices;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServices  implements INotificationServices {
    private final IUserRepository userRepository;
    private  final AuthService authService;
    private  final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private final FCMService fcmService;
    @Override
    public void saveFcmToken(FcmTokenRequest tokenRequest) {
        Long userId = authService.getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with Id: '" + userId + "' not found"));
        user.setFcmToken(tokenRequest.getFcmToken());
        userRepository.save(user);
    }
   /* @Override
    public Page<Notification> getNotifications(int pageNo, int pageSize) {
        Long userId = authService.getAuthenticatedUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "sentAt"));

        return notificationRepository.findByUserId(userId, pageable);
    }*/

   @Override
   public Page<NotificationResponse> getNotifications(int pageNo, int pageSize) {
       Long userId = authService.getAuthenticatedUserId();

       User user = userRepository.findById(userId)
               .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));

       Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "sentAt"));

       Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
       return notifications.map(notificationMapper::mapToDto);
   }
   @Override

    public void sendPrescriptionNotificationToPatient(Patient patient, User doctor, Prescription prescription) {
        if (patient.getFcmToken() != null && !patient.getFcmToken().isBlank()) {
            NotificationRequest notif = NotificationRequest.builder()
                    .title("New prescription")
                    .body("DR." + doctor.fullName() + " has added a new prescription for you.")
                    .token(patient.getFcmToken())
                    .build();

            try {
                fcmService.sendMessageToToken(notif);
                Notification entity = Notification.builder()
                        .title(notif.getTitle())
                        .body(notif.getBody())
                        .sentAt(LocalDateTime.now())
                        .type(NotificationType.MEDICATION)
                        .user(patient)
                        .prescription(prescription)
                        .build();
                notificationRepository.save(entity);
            } catch (Exception e) {
                System.out.println("Erreur FCM : " + e.getMessage());
            }
        }
    }



}