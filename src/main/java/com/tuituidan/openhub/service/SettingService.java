package com.tuituidan.openhub.service;

import com.tuituidan.openhub.config.AppProperties;
import com.tuituidan.openhub.util.FileNameValidator;
import com.tuituidan.openhub.util.IpUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

/**
 * SysSettingService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-06-04
 */
@Service
public class SettingService implements ApplicationRunner {

    private static final String SETTING_FILE = "settings.properties";

    @Resource
    private AppProperties appProperties;

    /**
     * getSysSetting
     *
     * @return Properties
     */
    public Properties getSetting() {
        File file = new File(SETTING_FILE);
        if (!file.exists()) {
            return new Properties();
        }
        try {
            EncodedResource encodedResource = new EncodedResource(new FileSystemResource(file),
                    StandardCharsets.UTF_8);
            return PropertiesLoaderUtils.loadProperties(encodedResource);
        } catch (Exception ex) {
            throw new IllegalArgumentException("系统配置加载失败", ex);
        }
    }

    /**
     * saveSysSetting
     *
     * @param properties properties
     */
    public void saveSetting(Properties properties) {
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(SETTING_FILE),
                StandardCharsets.UTF_8)) {
            properties.store(writer, "system setting");
        } catch (IOException ex) {
            throw new IllegalArgumentException("系统配置保存失败", ex);
        }
        FileNameValidator.clearCache();
        IpUtils.setLocalIp(StringUtils.defaultString(properties.getProperty("local-ip")));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file = new File(SETTING_FILE);
        if (file.exists()) {
            IpUtils.setLocalIp(StringUtils.defaultString(getSetting().getProperty("local-ip")));
            return;
        }
        Properties properties = new Properties();
        properties.putAll(appProperties.getFileExt());
        saveSetting(properties);
    }

}
