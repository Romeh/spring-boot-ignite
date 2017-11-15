package com.romeh.ignitemanager.repositories.impl;

import com.romeh.ignitemanager.entities.AlertEntry;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by romeh on 17/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class IgniteAlertsSoreTest {
    @Mock
    private Ignite ignite;
    @Mock
    Cache<String, List<AlertEntry>>  cache;
    @Mock
    IgniteCache IgniteCache;
    @InjectMocks
    private IgniteAlertsStore igniteAlertsStore;
    @Before
    public void setUp() throws Exception {
        when(ignite.getOrCreateCache(anyString())).thenReturn(IgniteCache);
        List<AlertEntry> entries=new ArrayList<>();
        entries.add(AlertEntry.builder().errorCode("errorCode").build());
        when(IgniteCache.get(anyString())).thenReturn(entries);
    }




    @Test
    public void getAllAlerts() throws Exception {
        assertEquals(igniteAlertsStore.getAlertForServiceId("serviceId").get(0).getErrorCode(),"errorCode");
    }


}