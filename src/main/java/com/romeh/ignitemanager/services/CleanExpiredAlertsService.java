package com.romeh.ignitemanager.services;

import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.entities.CacheNames;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * Created by romeh on 22/08/2017.
 */
@Service
public class CleanExpiredAlertsService {
    private static final Logger logger = LoggerFactory.getLogger(CleanExpiredAlertsService.class);


    @Autowired
    Ignite ignite;

    @Scheduled(initialDelayString = "${initialDelay}", fixedDelayString = "${fixedDelay}")
    public void cleanExpiredRecords(){
        // query the matching records first
        logger.debug("Starting the clean up job to clear the expired records");
        long towMinutesRange = System.currentTimeMillis()-900000;
        final IgniteCache<String, List<AlertEntry>> alertsCache = getAlertsCache();
        final String sql = "select * from AlertEntry where timestamp <= ?";
        SqlFieldsQuery query = new SqlFieldsQuery(sql);
        query.setArgs(towMinutesRange);
        final List<List<?>> to_Delete_Alerts = alertsCache.query(query).getAll();
        // then call remove all as this will remove the records from the cache and the persistent file system as sql delete will just delete it from the cache layer not the file system
        // or the persistent store
        if(to_Delete_Alerts!=null && !to_Delete_Alerts.isEmpty()){
            List<AlertEntry> toDelete=(List<AlertEntry>) to_Delete_Alerts.get(0).get(0);
            logger.debug("Finished cleaning out {} records",toDelete.size());
            alertsCache.removeAll(new HashSet(toDelete));

        }

    }

    // get alerts cache store
    protected IgniteCache<String, List<AlertEntry>> getAlertsCache() {

        return ignite.cache(CacheNames.Alerts.name());
    }
}
