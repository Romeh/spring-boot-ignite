package com.romeh.ignitemanager.config;

import java.util.Arrays;
import java.util.Collection;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ConnectorConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.MemoryConfiguration;
import org.apache.ignite.configuration.PersistentStoreConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.romeh.ignitemanager.entities.AlertConfigEntry;
import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.entities.CacheNames;

/**
 * Created by romeh on 16/08/2017.
 */
@Configuration
public class AlertManagerConfiguration {

    @Value("${mail.service.baseUrl}")
    private String baseUrl;
    @Value("${mail.service.user}")
    private String user;
    @Value("${mail.service.password}")
    private String password;
    @Value("${enableFilePersistence}")
    private boolean enableFilePersistence;
    @Value("${igniteConnectorPort}")
    private int igniteConnectorPort;
    @Value("${igniteServerPortRange}")
    private String igniteServerPortRange;
    @Value("${ignitePersistenceFilePath}")
    private String ignitePersistenceFilePath;
	private static final String DATA_CONFIG_NAME = "MyDataRegionConfiguration";

	@Bean
    IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		igniteConfiguration.setWorkDirectory(ignitePersistenceFilePath);
        igniteConfiguration.setClientMode(false);
        // durable file memory persistence
        if(enableFilePersistence){

	        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
	        dataStorageConfiguration.setStoragePath(ignitePersistenceFilePath + "/store");
	        dataStorageConfiguration.setWalArchivePath(ignitePersistenceFilePath + "/walArchive");
	        dataStorageConfiguration.setWalPath(ignitePersistenceFilePath + "/walStore");
	        dataStorageConfiguration.setPageSize(4 * 1024);
	        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
	        dataRegionConfiguration.setName(DATA_CONFIG_NAME);
	        dataRegionConfiguration.setInitialSize(100 * 1000 * 1000);
	        dataRegionConfiguration.setMaxSize(200 * 1000 * 1000);
	        dataRegionConfiguration.setPersistenceEnabled(true);
	        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);
	        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
	        igniteConfiguration.setConsistentId("RomehFileSystem");
        }
        // connector configuration
        ConnectorConfiguration connectorConfiguration=new ConnectorConfiguration();
        connectorConfiguration.setPort(igniteConnectorPort);
        // common ignite configuration
        igniteConfiguration.setMetricsLogFrequency(0);
        igniteConfiguration.setQueryThreadPoolSize(2);
        igniteConfiguration.setDataStreamerThreadPoolSize(1);
        igniteConfiguration.setManagementThreadPoolSize(2);
        igniteConfiguration.setPublicThreadPoolSize(2);
        igniteConfiguration.setSystemThreadPoolSize(2);
        igniteConfiguration.setRebalanceThreadPoolSize(1);
        igniteConfiguration.setAsyncCallbackPoolSize(2);
        igniteConfiguration.setPeerClassLoadingEnabled(false);
        igniteConfiguration.setIgniteInstanceName("alertsGrid");
        BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
        binaryConfiguration.setCompactFooter(false);
        igniteConfiguration.setBinaryConfiguration(binaryConfiguration);
        // cluster tcp configuration
        TcpDiscoverySpi tcpDiscoverySpi=new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder=new TcpDiscoveryVmIpFinder();
        // need to be changed when it come to real cluster
        tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47509"));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi());
        // cache configuration
        CacheConfiguration alerts=new CacheConfiguration();
        alerts.setCopyOnRead(false);
        // as we have one node for now
		alerts.setBackups(1);
        alerts.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		alerts.setName(CacheNames.Alerts.name());
		alerts.setDataRegionName(DATA_CONFIG_NAME);
		alerts.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC);
        alerts.setIndexedTypes(String.class,AlertEntry.class);

        CacheConfiguration alertsConfig=new CacheConfiguration();
        alertsConfig.setCopyOnRead(false);
        // as we have one node for now
		alertsConfig.setBackups(1);
        alertsConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		alertsConfig.setName(CacheNames.AlertsConfig.name());
        alertsConfig.setIndexedTypes(String.class,AlertConfigEntry.class);
		alertsConfig.setDataRegionName(DATA_CONFIG_NAME);
		alertsConfig.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC);
        igniteConfiguration.setCacheConfiguration(alerts,alertsConfig);
        return igniteConfiguration;
    }

    @Bean(destroyMethod = "close")
    Ignite ignite(IgniteConfiguration igniteConfiguration) throws IgniteException {
	    final Ignite ignite = Ignition.start(igniteConfiguration);
	    // Activate the cluster. Automatic topology initialization occurs
	    // only if you manually activate the cluster for the very first time.
	    ignite.cluster().active(true);
	    /*// Get all server nodes that are already up and running.
	    Collection<ClusterNode> nodes = ignite.cluster().forServers().nodes();
		// Set the baseline topology that is represented by these nodes.
	    ignite.cluster().setBaselineTopology(nodes);*/
	    return ignite;
    }



}
