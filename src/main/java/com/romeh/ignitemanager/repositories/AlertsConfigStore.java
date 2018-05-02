package com.romeh.ignitemanager.repositories;

import java.util.Optional;

import com.romeh.ignitemanager.entities.AlertConfigEntry;

/**
 * Created by romeh on 18/08/2017.
 */
public interface AlertsConfigStore {
    AlertConfigEntry getConfigForServiceIdCodeId(String serviceId, String codeId);
    void update(String serviceId, String codeId, AlertConfigEntry alertConfigEntry);
    Optional<AlertConfigEntry> getConfigForServiceIdCodeIdCount(String serviceId, String codeId);
}
