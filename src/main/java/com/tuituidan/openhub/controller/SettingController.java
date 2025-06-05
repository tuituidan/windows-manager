package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.service.SettingService;
import java.util.Properties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemSettingController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-06-04
 */
@RestController
@RequestMapping("/api/v1/setting")
@Slf4j
public class SettingController {

    @Resource
    private SettingService settingService;

    /**
     * getSetting
     *
     * @return Properties
     */
    @GetMapping
    public Properties getSetting() {
        return settingService.getSetting();
    }

    /**
     * saveSetting
     *
     * @param properties properties
     */
    @PostMapping
    public void saveSetting(@RequestBody Properties properties) {
        settingService.saveSetting(properties);
    }

}
