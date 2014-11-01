package net.mikesu.fastdfs;

import java.util.List;

import net.mikesu.fastdfs.client.StorageClient;
import net.mikesu.fastdfs.client.StorageClientFactory;
import net.mikesu.fastdfs.client.TrackerClient;
import net.mikesu.fastdfs.client.TrackerClientFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastdfsClientFactory {
	
	private static volatile FastdfsClient fastdfsClient;
	private static String config_file="config/fdfs_client.conf";
	
	public static FastdfsClient getFastdfsClient() throws ConfigurationException {
		return getFastdfsClient(config_file);
	}
	
	public static FastdfsClient getFastdfsClient(List<String> trackerAddrs){
		FastdfsClientConfig config = new FastdfsClientConfig();
		config.setTrackerAddrs(trackerAddrs);
		return getFastdfsClient(new FastdfsClientConfig());
	}
	
	public static FastdfsClient getFastdfsClient(String configFileName) throws ConfigurationException{
		FastdfsClientConfig config = new FastdfsClientConfig(configFileName);
		return getFastdfsClient(config);
	}
	
	public static FastdfsClient getFastdfsClient(FastdfsClientConfig config){
		if (fastdfsClient == null) {
			synchronized (FastdfsClient.class) {
				if (fastdfsClient == null) {
					int connectTimeout = config.getConnectTimeout();
					int networkTimeout = config.getNetworkTimeout();
					TrackerClientFactory trackerClientFactory = new TrackerClientFactory(connectTimeout, networkTimeout);
					StorageClientFactory storageClientFactory = new StorageClientFactory(connectTimeout, networkTimeout);
					GenericKeyedObjectPoolConfig trackerClientPoolConfig = config.getTrackerClientPoolConfig();
					GenericKeyedObjectPoolConfig storageClientPoolConfig = config.getStorageClientPoolConfig();
					GenericKeyedObjectPool<String,TrackerClient> trackerClientPool = new GenericKeyedObjectPool<String,TrackerClient>(trackerClientFactory, trackerClientPoolConfig);
					GenericKeyedObjectPool<String,StorageClient> storageClientPool = new GenericKeyedObjectPool<String,StorageClient>(storageClientFactory, storageClientPoolConfig);
					List<String> trackerAddrs = config.getTrackerAddrs();
					fastdfsClient = new FastdfsClientImpl(trackerAddrs,trackerClientPool,storageClientPool);
				}
			}
		}
		return fastdfsClient;
	}
	
}
