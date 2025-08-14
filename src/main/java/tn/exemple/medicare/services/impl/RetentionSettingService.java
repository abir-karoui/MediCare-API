package tn.exemple.medicare.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.RetentionSetting;
import tn.exemple.medicare.repositories.RetentionSettingRepository;

@Service
public class RetentionSettingService {

    @Autowired
    private RetentionSettingRepository repository;

    private static final Long SETTINGS_ID = 1L;

    public RetentionSetting getSettings() {
        return repository.findById(SETTINGS_ID).orElseGet(() -> {
            RetentionSetting setting = new RetentionSetting();
            setting.setId(SETTINGS_ID);
            setting.setActivityRetentionDays(30);
            setting.setNotificationRetentionDays(30);
            return repository.save(setting);
        });
    }

    public RetentionSetting updateSettings(RetentionSetting newSetting) {
        newSetting.setId(SETTINGS_ID);
        return repository.save(newSetting);
    }


}

