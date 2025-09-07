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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getLoginStatsLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6); // dernière semaine

        LocalDateTime startDateTime = weekAgo.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(LocalTime.MAX);

        Locale locale = Locale.ENGLISH; // même locale partout

        // Récupérer toutes les activités Login de la dernière semaine
        List<ActivityLog> logs = activityLogRepository.findByTitleAndTimestampBetween(
                "Login", startDateTime, endDateTime
        );

        // Grouper par nom de jour et compter
        Map<String, Long> stats = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        // Construire la map complète avec tous les jours de la semaine
        Map<String, Long> completeStats = new LinkedHashMap<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate date = weekAgo.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
            completeStats.put(dayName, stats.getOrDefault(dayName, 0L));
        }

        return completeStats;
    }


}
