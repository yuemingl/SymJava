package lambdacloud.core;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lambdacloud.net.CloudClient;

public class CloudConfig {
	protected String target = "local";
	protected static CloudConfig globalConfig = null;

	protected List<CloudClient> clients = new ArrayList<CloudClient>();
	protected CloudClient currentClient = null;

	/**
	 * Parameter target can be
	 * 1. "local": Locally compile and run
	 * 2. "<lambda_cloud_auth_file>": Compile and run on www.lambdacloud.io
	 * 
	 * @param target
	 */
	public CloudConfig(String target) {
		if(target == null) return;
		this.target = target;
		if(!target.equals("local")) {
			try {
				Path path = Paths.get(System.getProperty("user.dir")+"/"+target);
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
	}
	
	/**
	 * Get an instance of CloudConfig used as a local configuration
	 * 
	 * @param target
	 * @return
	 */
	public static CloudConfig instance(String target) {
		return new CloudConfig(target);
	}
	
	/**
	 * Set a target used as global configuration in your application
	 * @param target
	 */
	public static CloudConfig setGlobalTarget(String target) {
		globalConfig = new CloudConfig(target);
		return globalConfig;
	}
	
	public static CloudConfig getGlobalConfig() {
		return globalConfig;
	}

	public boolean isLocal() {
		return target.equalsIgnoreCase("local");
	}

	/**
	 * Print the configuration of the target environment
	 * @return
	 */
	public String printTargetInfo() {
		return "16 CPU, 64GB RAM";
	}
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
		if(this.isLocal())
			return null;
		return clients.get(index);
	}
	
	public int getTotalNumClients() {
		if(this.isLocal())
			return 1;
		return clients.size();
	}
	
	public CloudClient currentClient() {
		return currentClient;
	}
	
	public void useClient(CloudClient client) {
		if(this.isLocal()) 
			return;
		this.currentClient = client;
	}
	
	public void shutDown() {
		for(CloudClient client : clients)
			client.shutDown();
	}
}
