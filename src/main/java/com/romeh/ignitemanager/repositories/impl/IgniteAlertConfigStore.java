package com.romeh.ignitemanager.repositories.impl;

import com.romeh.ignitemanager.entities.AlertConfigEntry;
import com.romeh.ignitemanager.entities.CacheNames;
import com.romeh.ignitemanager.exception.ResourceNotFoundException;
import com.romeh.ignitemanager.repositories.AlertsConfigStore;
import org.apache.ignite.Ignite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.Optional;

/**
 * Created by romeh on 18/08/2017.
 */
@Component
public class IgniteAlertConfigStore implements AlertsConfigStore {

    private static final Logger logger = LoggerFactory.getLogger(IgniteAlertConfigStore.class);

    @Autowired
    private Ignite ignite;

    @Override
    public AlertConfigEntry getConfigForServiceIdCodeId(String serviceId, String codeId) {
        return Optional.ofNullable(getAlertsConfigCache().get(serviceId + "_" + codeId))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Alert config for %s with %s not found", serviceId,codeId)));
    }

    @Override
    public void update(String serviceId, String codeId, AlertConfigEntry alertConfigEntry) {
        getAlertsConfigCache().put(serviceId + "_" + codeId, alertConfigEntry);
    }

    @Override
    public Optional<AlertConfigEntry> getConfigForServiceIdCodeIdCount(String serviceId, String codeId) {
        return Optional.ofNullable(getAlertsConfigCache().get(serviceId + "_" + codeId));

    }


    public Cache<String, AlertConfigEntry> getAlertsConfigCache() {
        return ignite.getOrCreateCache(CacheNames.AlertsConfig.name());
    }
}
