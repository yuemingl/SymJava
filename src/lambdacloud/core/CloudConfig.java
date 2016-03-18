package lambdacloud.core;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lambdacloud.net.CloudClient;

/**
 * An instance of CloudConfig is constructed from a configuration file.
 * All the available servers information are store in the CloudConfig object.
 */
public class CloudConfig {
	protected String configFile;
	protected static CloudConfig globalConfig = new CloudConfig();

	protected List<CloudClient> clients = new ArrayList<CloudClient>();
	protected CloudClient currentClient;

	/**
	 * Construct a CloudConfig object which allows all the computations run on local machine
	 */
	public CloudConfig() {
	}
	
	/**
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
	 * Set a configuration file used as global configuration
	 * 
	 * @param configFile
	 */
	public static CloudConfig setGlobalConfig(String configFile) {
		globalConfig = new CloudConfig(configFile);
		return globalConfig;
	}
	
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

//	/**
//	 * Print the configuration of the target environment
//	 * @return
//	 */
//	public String printTargetInfo() {
//		return "16 CPU, 64GB RAM";
//	}
	
	/*
	public String getHost() {
		//return "localhost";
		//return "ec2-54-200-107-134.us-west-2.compute.amazonaws.com"; //
		//return "ec2-52-27-4-226.us-west-2.compute.amazonaws.com"; //c4.large
		//return "104.197.57.20";//gcloud
		return "vm1-yliu.cloudapp.net";//MA
	}
	
	public int getPort() {
		return 8322;
		
	}
	*/
	
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
