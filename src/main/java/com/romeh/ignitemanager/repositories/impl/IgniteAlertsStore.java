package com.romeh.ignitemanager.repositories.impl;

import com.romeh.ignitemanager.entities.AlertConfigEntry;
import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.entities.CacheNames;
import com.romeh.ignitemanager.exception.ResourceNotFoundException;
import com.romeh.ignitemanager.repositories.AlertsConfigStore;
import com.romeh.ignitemanager.repositories.AlertsStore;
import com.romeh.ignitemanager.services.MailService;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class IgniteAlertsStore implements AlertsStore {
    private static final Logger logger = LoggerFactory.getLogger(IgniteAlertsStore.class);

    @Autowired
    private Ignite ignite;

    @Autowired
    private MailService mailService;

    @Autowired
    private AlertsConfigStore alertsConfigStore;

    @Override
    public List<AlertEntry> getAlertForServiceId(String serviceId) {
        final String sql = "serviceId = ?";
        SqlQuery<String, AlertEntry> query = new SqlQuery<>(AlertEntry.class, sql);
        query.setArgs(serviceId);
        return Optional.ofNullable(getAlertsCache().query(query).getAll()
                .stream()
                .map(Cache.Entry::getValue)
                .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Alert for %s not found", serviceId)));
    }

    @Override
    public void updateAlertEntry(String serviceId, String serviceCode, AlertEntry alertEntry) {
        final IgniteCache<String, AlertEntry> alertsCache = getAlertsCache();
        // update the alert entry via cache invoke for atomicity
        alertsCache.invoke(alertEntry.getAlertId(), (mutableEntry, objects) -> {
            if (mutableEntry.exists() && mutableEntry.getValue() != null) {
                logger.debug("updating alert entry into the cache store invoke: {},{}", serviceId, serviceCode);
                mutableEntry.setValue(alertEntry);
            } else {
                throw new ResourceNotFoundException(String.format("Alert for %s with %s not found", serviceId, serviceCode));
            }
            // by api design nothing needed here
            return null;
        });
    }

    @Override
    public List<AlertEntry> getAllAlerts() {
        final String sql = "select * from AlertEntry";
        SqlQuery<String, AlertEntry> query = new SqlQuery<>(AlertEntry.class, sql);
        return getAlertsCache().query(query).getAll()
                .stream()
                .map(Cache.Entry::getValue)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteAlertEntry(String alertId) {
        final IgniteCache<String, AlertEntry> alertsCache = getAlertsCache();
        alertsCache.remove(alertId);
    }

    @Override
    public void createAlertEntry(AlertEntry alertEntry) {
        // get the alert config if any
        final Optional<AlertConfigEntry> configForServiceIdCodeIdCount =
                alertsConfigStore.getConfigForServiceIdCodeIdCount(alertEntry.getServiceId(), alertEntry.getErrorCode());
        // get the max count of alerts before sending mail
        final int maxCount = configForServiceIdCodeIdCount.isPresent() ?
                configForServiceIdCodeIdCount.get().getMaxCount() : 1;
        final String mailTemplate = configForServiceIdCodeIdCount.isPresent() ?
                configForServiceIdCodeIdCount.get().getMailTemplate() : "ticket";
        // define the expiry of the entry in the cache
        final IgniteCache<String, AlertEntry> alertsCache = getAlertsCache();
        // insert into the key value store
        alertsCache.put(alertEntry.getAlertId(), alertEntry);
        // send the mail notification if max is there
        final SqlFieldsQuery sql = new SqlFieldsQuery("select count(*) from AlertEntry where serviceId = '" + alertEntry.getServiceId() + "' and errorCode = '" + alertEntry.getErrorCode() + "'");
        final List<List<?>> count = alertsCache.query(sql).getAll();
        if (count != null && !count.isEmpty()) {
            final Long result = (Long) count.get(0).get(0);
            if (result >= maxCount) {
                logger.debug("max alerts count is reached for : {}, start sending mail alert {}", alertEntry.toString());
                sendMail(alertEntry, configForServiceIdCodeIdCount.isPresent() ? configForServiceIdCodeIdCount.get().getEmails() : Collections.emptyList(), mailTemplate);
            }
        }
    }

    @Async
    protected void sendMail(AlertEntry alertEntry, List<String> emails, String mailTemplate) {
        // send the mail then delete the entry
        final boolean doneSending = mailService.sendAlert(alertEntry, emails, mailTemplate);
        if (doneSending) {
            cleanAllAlertEntriesForThatErrorCodeAndServiceCode(alertEntry.getServiceId(), alertEntry.getErrorCode());
        }

    }
    // clean all alerts for that service code and service id
    private void cleanAllAlertEntriesForThatErrorCodeAndServiceCode(String serviceId, String errorId) {

        // commenting it out for testing without clean-up
     /*   // query the matching records first
        final String sql = "serviceId = ? and errorCode= ?";
        SqlQuery<String,AlertEntry> query = new SqlQuery(AlertEntry.class,sql);
        query.setArgs(serviceId, errorId);
        final List<Cache.Entry<String, AlertEntry>> to_Delete_Alerts = getAlertsCache().query(query).getAll();
        // then call remove all as this will remove the records from the cache and the persistent file system
        // as sql delete will just delete it from the cache layer not the file system
        // or the persistent store
        if(to_Delete_Alerts!=null && !to_Delete_Alerts.isEmpty()){
            getAlertsCache().removeAll(new HashSet(to_Delete_Alerts.stream().map(stringAlertEntryEntry -> stringAlertEntryEntry.getKey()).collect(Collectors.toList())));
        }*/

    }

    // get alerts cache store
    protected IgniteCache<String, AlertEntry> getAlertsCache() {
        return ignite.cache(CacheNames.Alerts.name());
    }


}
