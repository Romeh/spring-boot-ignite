package com.romeh.ignitemanager.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by romeh on 08/08/2017.
 */
@Configuration
@ConfigurationProperties(prefix = "alerts")
@Getter
@Setter
public class AlertsConfiguration {

    private List<AlertConfigEntry> alertConfigurations;
}
