package com.romeh.ignitemanager.services;

import com.romeh.ignitemanager.entities.AlertConfigEntry;
import com.romeh.ignitemanager.entities.AlertsConfiguration;
import com.romeh.ignitemanager.entities.CacheNames;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.cache.Cache;

/**
 * Created by romeh on 11/08/2017.
 */
@Service
public class DataLoaderService {


    @Autowired
    private Ignite ignite;

    @Autowired
    private AlertsConfiguration alertsConfig;


    @PostConstruct
    public void init() {
        final Cache<String, AlertConfigEntry> alertsConfigCache = ignite.getOrCreateCache(CacheNames.AlertsConfig.name());
        this.alertsConfig.getAlertConfigurations().forEach(alertConfigEntity -> {
            alertsConfigCache.putIfAbsent(alertConfigEntity.getServiceCode() + "_" + alertConfigEntity.getErrorCode(),
                    alertConfigEntity);

        });

    }


}
