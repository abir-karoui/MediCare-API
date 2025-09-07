package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.dto.FcmTokenRequest;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationResponse;
import tn.exemple.medicare.services.INotificationServices;
import tn.exemple.medicare.services.impl.NotificationServices;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationServices notificationService;
    @PostMapping("/register-token")
    public ResponseEntity<String> saveFcmToken(@RequestBody FcmTokenRequest tokenRequest) {
        notificationService.saveFcmToken(tokenRequest);
        return ResponseEntity.ok("FCM Token saved successfully!");
    }
    @GetMapping("/all")
    public Page<NotificationResponse> getNotifications(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return notificationService.getNotifications(pageNo, pageSize);
    }
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationCount() {
        int count = notificationService.getUnreadNotificationCount();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/notif/AccountVerification")
    public Page<NotificationResponse> getAccountVerificationNotifications(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return notificationService.getAccountVerificationNotifications(pageNo, pageSize);
    }

}

