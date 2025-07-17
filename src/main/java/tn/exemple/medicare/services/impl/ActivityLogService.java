package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.entities.ActivityLog;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationResponse;
import tn.exemple.medicare.repositories.ActivityLogRepository;
import tn.exemple.medicare.services.IActivityLogService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService implements IActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    public void logActivity(String title, String performedBy, String description) {
        ActivityLog log = new ActivityLog();
        log.setTitle(title);
        log.setPerformedBy(performedBy);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> getActivities(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"));

        return activityLogRepository.findAll(pageable);
    }


}
