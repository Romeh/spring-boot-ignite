package com.romeh.ignitemanager.repositories.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.cache.Cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.processors.cache.CacheEntryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.entities.CacheNames;

/**
 * Created by romeh on 17/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class IgniteAlertsSoreTest {
	@Mock
	private Ignite ignite;
	@Mock
	QueryCursor queryCursor;
	@Mock
	IgniteCache igniteCache;
	@InjectMocks
	private IgniteAlertsStore igniteAlertsStore;

	@Before
	public void setUp() {
		Cache.Entry entry = new CacheEntryImpl("serviceId", AlertEntry.builder().errorCode("errorCode").build());
		when(ignite.cache(CacheNames.Alerts.name())).thenReturn(igniteCache);
		when(igniteAlertsStore.getAlertsCache()).thenReturn(igniteCache);
		when(igniteCache.query(any(SqlQuery.class))).thenReturn(queryCursor);
		when(queryCursor.getAll()).thenReturn(Arrays.asList(entry));

	}

	@Test
	public void getAllAlerts() {
		assertEquals(igniteAlertsStore.getAlertForServiceId("serviceId").get(0).getErrorCode(), "errorCode");
	}


}