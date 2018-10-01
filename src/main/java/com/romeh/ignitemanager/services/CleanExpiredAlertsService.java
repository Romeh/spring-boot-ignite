package com.romeh.ignitemanager.services;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.entities.CacheNames;

/**
 * Created by romeh on 22/08/2017.
 */
@Service
public class CleanExpiredAlertsService {
	private static final Logger logger = LoggerFactory.getLogger(CleanExpiredAlertsService.class);


	@Autowired
	Ignite ignite;

	@Scheduled(initialDelayString = "${initialDelay}", fixedDelayString = "${fixedDelay}")
	// un comment if u need to test it otherwise it will clear ur alerts cache during startup
	public void cleanExpiredRecords() {
      /*  // query the matching records first
        logger.debug("Starting the clean up job to clear the expired records");
        long towMinutesRange = System.currentTimeMillis()-900000;
        final IgniteCache<String, List<AlertEntry>> alertsCache = getAlertsCache();
        final String sql = "select * from AlertEntry where timestamp <= ?";
        SqlQuery<String,AlertEntry> query = new SqlQuery(AlertEntry.class,sql);
        query.setArgs(towMinutesRange);
        final List<Cache.Entry<String, AlertEntry>> toDeleteAlerts = alertsCache.query(query).getAll();
        // then call remove all as this will remove the records from the cache and the persistent file system as sql delete will just delete it from the cache layer not the file system
        // or the persistent store
        if(toDeleteAlerts!=null && !toDeleteAlerts.isEmpty()){
            logger.debug("Finished cleaning out {} records",toDeleteAlerts.size());
            alertsCache.removeAll(new HashSet(toDeleteAlerts
                    .stream()
                    .map(Cache.Entry::getKey)
                    .collect(Collectors.toList())));

        }*/

	}

	// get alerts cache store
	protected IgniteCache<String, List<AlertEntry>> getAlertsCache() {

		return ignite.cache(CacheNames.Alerts.name());
	}
}
