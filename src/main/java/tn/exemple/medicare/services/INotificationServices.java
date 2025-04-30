package tn.exemple.medicare.services;

import org.springframework.data.domain.Page;
import tn.exemple.medicare.entities.dto.FcmTokenRequest;
import tn.exemple.medicare.entities.notification.Notification;

public interface INotificationServices {
    void saveFcmToken(FcmTokenRequest tokenRequest);
    Page<Notification> getNotifications (int pageNo, int pageSize) ;
}
