package tn.exemple.medicare.services;

import org.springframework.data.domain.Page;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.FcmTokenRequest;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationResponse;
import tn.exemple.medicare.entities.prescription.Prescription;

public interface INotificationServices {
    void saveFcmToken(FcmTokenRequest tokenRequest);
    Page<NotificationResponse> getNotifications (int pageNo, int pageSize) ;
    void sendPrescriptionNotificationToPatient(Patient patient, User doctor, Prescription prescription);
}
