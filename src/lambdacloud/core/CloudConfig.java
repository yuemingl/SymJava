package lambdacloud.core;

public class CloudConfig {
	String config;

	/**
	 * Parameter config can be
	 * 1. "local": Locally compile and run
	 * 2. "<lambda_cloud_auth_file>.lca": Compile and run on www.lambdacloud.io
	 * 
	 * @param target
	 */
	public CloudConfig(String config) {
		this.config = config;
	}
	
	public boolean isLocal() {
		return config.equalsIgnoreCase("local");
	}

	/**
	 * Print the configuration of target environment
	 * @return
	 */
	public String printTargetInfo() {
		return "16 CPU, 64GB RAM";
	}
}
