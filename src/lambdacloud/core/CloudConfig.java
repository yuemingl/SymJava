package lambdacloud.core;

public class CloudConfig {
	private static String target;

	/**
	 * Parameter target can be
	 * 1. "local": Locally compile and run
	 * 2. "<lambda_cloud_auth_file>.lca": Compile and run on www.lambdacloud.io
	 * 
	 * @param target
	 */
	public static void setTarget(String target) {
		CloudConfig.target = target;
	}
	
	public static boolean isLocal() {
		return target.equalsIgnoreCase("local");
	}

	/**
	 * Print the configuration of target environment
	 * @return
	 */
	public static String printTargetInfo() {
		return "16 CPU, 64GB RAM";
	}
	
	public static String getHost() {
		return "127.0.0.1";
	}
	
	public static int getPort() {
		return 8322;
		
	}
}
