package tn.exemple.medicare.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.entities.RetentionSetting;
import tn.exemple.medicare.repositories.ActivityLogRepository;
import tn.exemple.medicare.repositories.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class CleanupService {

    @Autowired
    private RetentionSettingService settingService;

    @Autowired
    private ActivityLogRepository activityRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Scheduled(cron = "0 43 0 * * ?")
    @Transactional
    public void cleanOldData() {
        RetentionSetting setting = settingService.getSettings();
        LocalDateTime activityThreshold = LocalDateTime.now().minusDays(setting.getActivityRetentionDays());
        LocalDateTime notificationThreshold = LocalDateTime.now().minusDays(setting.getNotificationRetentionDays());

        activityRepo.deleteByTimestampBefore(activityThreshold);
        notificationRepo.deleteBySentAtBefore(notificationThreshold);
    }
}
