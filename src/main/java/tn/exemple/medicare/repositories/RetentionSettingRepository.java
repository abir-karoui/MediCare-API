package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.RetentionSetting;

@Repository
public interface RetentionSettingRepository extends JpaRepository<RetentionSetting, Long> {
}
