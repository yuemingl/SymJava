package lambdacloud.core;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lambdacloud.net.CloudClient;

/**
 * An instance of CloudConfig contains the information of available servers.
 * A global instance of CloudConfig (call getGlobalConfg()) is used in the 
 * computation if no CloudConfig is provided. The default value of the global
 * configure of servers is simulated by multi-thread locally.
 * 
 */
public class CloudConfig {
	protected String configFile;
	protected static CloudConfig globalConfig = new CloudConfig();

	protected List<CloudClient> clients = new ArrayList<CloudClient>();
	protected CloudClient currentClient;

	/**
	 * Construct a CloudConfig object which allows all the computations 
	 * run on local machine and the servers are simulated by multi-thread
	 */
	public CloudConfig() {
	}
	
	/**
	 * Construct a instance from a configuration file
	 * 
	 * @param configFile
	 */
	public CloudConfig(String configFile) {
		if(configFile == null) {
			System.out.println("Using local config.");
			return;
		}
		this.configFile = configFile;
		try {
			Path path = Paths.get(System.getProperty("user.dir")+"/conf/"+configFile);
			System.out.println("Using config file: "+path);
			List<String> hosts = Files.readAllLines(path, Charset.forName("UTF-8"));
			for(String host : hosts) {
				if(host.trim().length() == 0) 
					continue;
				String[] arr = host.split(":");
				if(arr.length == 2) {
					CloudClient c = new CloudClient(arr[0], Integer.valueOf(arr[1]));
					c.connect();
					System.out.println(host);
					clients.add(c);
					if(currentClient == null) {
						currentClient = c;
					}
				} else {
					System.out.println("Can not parse host: "+host);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the global configuration by providing a configuration file
	 * 
	 * @param configFile
	 */
	public static CloudConfig setGlobalConfig(String configFile) {
		globalConfig = new CloudConfig(configFile);
		return globalConfig;
	}
	
	/**
	 * Set config as the global configuration
	 * @param config
	 * @return
	 */
	public static CloudConfig setGlobalConfig(CloudConfig config) {
		globalConfig = config;
		return globalConfig;
	}
	
	public static CloudConfig getGlobalConfig() {
		if(globalConfig == null) {
			throw new RuntimeException("Global CloudConfig is not specified!");
		}
		return globalConfig;
	}

	public boolean isLocalConfig() {
		return null == configFile;
	}

	public CloudClient getClientByIndex(int index) {
		if(this.isLocalConfig())
			return null;
		return clients.get(index);
	}
	
	public int getNumClients() {
		if(this.isLocalConfig())
			return 1;
		return clients.size();
	}
	
	public CloudClient getCurrentClient() {
		return currentClient;
	}
	
	public void setCurrentClient(CloudClient client) {
		if(this.isLocalConfig()) 
			return;
		this.currentClient = client;
	}

	public void setCurrentClient(int index) {
		if(this.currentClient == null)
			return;
		this.currentClient = clients.get(index);
	}
	
	public void reconnectAll() {
		for(CloudClient client : clients)
			try {
				client.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void disconnectAll() {
		for(CloudClient client : clients)
			client.shutDown();
	}
}
