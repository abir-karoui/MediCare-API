package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
    void deleteByTimestampBefore(LocalDateTime dateTime);
    List<ActivityLog> findByTitleAndTimestampBetween(String title, LocalDateTime start, LocalDateTime end);

}
