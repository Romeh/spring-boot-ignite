package com.romeh.ignitemanager.services;

import com.romeh.ignitemanager.entities.AlertConfigEntry;
import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.repositories.AlertsConfigStore;
import com.romeh.ignitemanager.repositories.AlertsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by romeh on 09/08/2017.
 */
@Service
public class AlertsService {
    private static final Logger logger = LoggerFactory.getLogger(AlertsService.class);

    @Autowired
    private AlertsStore alertsStore;

    @Autowired
    private AlertsConfigStore alertsConfigStore;


    public void createAlertEntry(AlertEntry alertEntry) {
        logger.debug("createAlertEntry service call with {}",alertEntry.toString());
        alertEntry.setAlertId(UUID.randomUUID().toString());
        alertEntry.setTimestamp(System.currentTimeMillis());
        alertsStore.createAlertEntry(alertEntry);

    }

    public List<AlertEntry> getAlertForServiceId(String serviceId) {
        logger.debug("GetAlertForServiceId service call with {}",serviceId);
        return alertsStore.getAlertForServiceId(serviceId);
    }


    public void updateAlertEntry(String serviceId, String serviceCode, AlertEntry alertEntry) {
        logger.debug("updateAlertEntry service call with {}, {}, {}",serviceId,serviceCode,alertEntry.toString());
        alertsStore.updateAlertEntry(serviceId, serviceCode, alertEntry);
    }


    public List<AlertEntry> getAllAlerts() {
        logger.debug("getAllAlerts service call");
        return alertsStore.getAllAlerts();
    }


    public void deleteAlertEntry(String alertId) {
        logger.debug("deleteAlertEntry service call: {}, {}",alertId);
        alertsStore.deleteAlertEntry(alertId);
    }


    public AlertConfigEntry getConfigForServiceIdCodeId(String serviceId, String codeId) {
        logger.debug("getConfigForServiceIdCodeId service call: {},{}",serviceId,codeId);
        return alertsConfigStore.getConfigForServiceIdCodeId(serviceId, codeId);
    }


    public void updateAlertConfig(String serviceId, String codeId, AlertConfigEntry alertConfigEntry) {
        logger.debug("updateAlertConfig service call: {}, {}, {}",serviceId,codeId,alertConfigEntry.toString());
        alertsConfigStore.update(serviceId, codeId, alertConfigEntry);
    }


}
