package com.tuituidan.openhub.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AppPropertiesConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025/3/28
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String cmdStartService;

    private String cmdListService;

    private String cmdListServiceWhere;

    private Map<String, String> fileExt;

}
