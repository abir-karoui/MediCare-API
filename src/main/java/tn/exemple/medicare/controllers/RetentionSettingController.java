package tn.exemple.medicare.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.RetentionSetting;
import tn.exemple.medicare.services.impl.RetentionSettingService;

@RestController
@RequestMapping("/admin/retention")
public class RetentionSettingController {

    @Autowired
    private RetentionSettingService service;

    @GetMapping
    public RetentionSetting getSettings() {
        return service.getSettings();
    }

    @PutMapping
    public RetentionSetting updateSettings(@RequestBody RetentionSetting setting) {
        return service.updateSettings(setting);
    }
}
