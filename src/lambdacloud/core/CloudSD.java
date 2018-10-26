package lambdacloud.core;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import lambdacloud.core.lang.LCAssign;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudSDHandler;
import lambdacloud.net.CloudSDRespHandler;
import lambdacloud.net.CloudSDResp;
import lambdacloud.net.CloudClient;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

/**
 * Cloud Shared Data (CloudSD)
 * <br>
 * An instance of CloudSD represents a shared data set on the cloud server.
 * The data set can be created on local machine and stored to the cloud side.
 * A data set on the cloud server can be download to local machine by 
 * providing its name.
 * <br>
 * 
 * priority:
 * 1. csd://ip_address/var_name
 * 2. useCloudConfig
 * 3. globalCloudConfig
 * 
 * 
 * <br>
 * Example:
 * <p><blockquote><pre>
 *     CloudSD data = new CloudSD("myvar").init(new double[]{1, 2, 3, 4, 5});
 *     data.push(); // Push data onto cloud
 *     if(data.fetch()) {
 *       for(double d : data.getData()) {
 *         System.out.println(d);
 *       }
 *     }
 * </pre></blockquote>
 *
 */
public class CloudSD extends Symbol {
	protected double[] data = new double[0];
	protected boolean isOnCloud = false;
	
	protected CloudConfig localConfig = null;
	
	protected boolean isReady = true;//Indicate if the result of a function is under evaluating 
	protected boolean isAsync = false; //Test purpose: isAsync==true when isReady==false

	private static AtomicInteger cdsVarNameGenerator = new AtomicInteger(0);
	protected boolean isGenName = false; //A flag to indicate if the name is a generated name

	/**
	 * Construct an instance with a generated name
	 */
	public CloudSD() {
		super(generateName());
		this.isGenName = true;
	}

	/**
	 * Construct an instance with a given name
	 * 
	 * @param name
	 */
	public CloudSD(String name) {
		super(name);
	}
	
	/**
	 * Construct a CloudSD object with a generated name based on a given expression.
	 * This constructor is used to create a new instance of CloudSD from other CloudSD.
	 * <br>
	 * An example application is in the iterating algorithms as below:
	 *	for(int i=0; i<maxIter; i++) {
	 *		func.apply(output, input);
	 *		Expr update = input + 1.0*output;
	 *		input = update; //Cast update to type CloudSD
	 *	}
	 *
	 * @param expr
	 */
	public CloudSD(Expr expr) {
		super(generateName());
		this.isGenName = true;
		this.compile(this.label, expr);
	}
	
	public CloudSD(String name, Expr expr) {
		super(name);
		this.compile(name, expr);
	}

	public CloudSD(CloudConfig config) {
		super(generateName());
		this.isGenName = true;
		this.localConfig = config;
	}

	public CloudSD(CloudConfig config, String name) {
		super(name);
		this.localConfig = config;
	}
	
	public CloudSD(CloudConfig config, Expr expr) {
		super(generateName());
		this.isGenName = true;
		this.localConfig = config;
		this.compile(this.label, expr);
	}
	
	public CloudSD(CloudConfig config, String name, Expr expr) {
		super(name);
		this.localConfig = config;
		this.compile(name, expr);
	}

	/**
	 * Initialize the cloud variable with the given array.
	 * The new cloud variable simply wrap the array; that is,
	 * it is backed by the given array. Any modifications to the 
	 * cloud variable will cause the array to be modified and vice versa.
	 * @param array
	 * @return
	 */
	public CloudSD init(double ...array) {
		this.data = array;
		return this;
	}
	
	private static String generateName() {
		return "csd"+cdsVarNameGenerator.incrementAndGet();
	}
	
	/**
	 * Use the given cloud configuration other than the global one
	 * @param conf
	 */
	public void setCloudConfig(CloudConfig conf) {
		this.localConfig = conf;
	}
	
	/**
	 * Return current cloud configuration. Default is the global configuration.
	 * @return
	 */
	public CloudConfig getCloudConfig() {
		if(this.localConfig != null)
			return this.localConfig;
		return CloudConfig.getGlobalConfig();
	}
	
	/**
	 * Set the value of the backed array at index
	 * @param index
	 * @param value
	 */
	public void setData(int index, double value) {
		data[index] = value;
	}
	
	/**
	 * Get the value of the backed array at index. If the data on cloud need to be returned
	 * call fetch() first.
	 * 
	 * @param index
	 * @return
	 */
	public double getData(int index) {
		return data[index];
	}
	
	/**
	 * Return the backed array (local data). If the data on cloud need to be returned
	 * call fetch() first.
	 * 
	 * @return
	 */
	public double[] getData() {
		return data;
	}
	
	/**
	 * Resize the backed array. Old data will be copied to the new backed array
	 * if the new size is larger than the old size otherwise the data that beyond
	 * the new size will be discarded.
	 * @param size
	 * @return
	 */
	public CloudSD resize(int size) {
		if(this.data == null)
			this.data = new double[size];
		else {
			double[] newdata = new double[size];
			if(size > data.length) {
				System.arraycopy(this.data, 0, newdata, 0, this.data.length);
			} else {
				System.arraycopy(this.data, 0, newdata, 0, size);
			}
			this.data = newdata;
		}
		return this;
	}
	
	/**
	 * Return the length of the backed array
	 * @return
	 */
	public int size() {
		return data.length;
	}
	
	/**
	 * Return the length of the backed array
	 * @return
	 */
	public int length() {
		return data.length;
	}
	
	/**
	 * Return the name of the cloud variable. The name is the identifier
	 * of the cloud variable on the cloud server. Any local instance of 
	 * CloudSD has the same name will be assumed to be the same variable 
	 * on the cloud side.
	 * @return
	 */
	public String getName() {
		String[] arr = this.label.split("/");
		if(arr.length > 1)
			return arr[arr.length-1];
		else
			return this.label;
	}
	
	public String getFullName() {
		return this.label;
	}
	
	private String[] parseName(String name) {
		if(name.startsWith("csd://")) {
			String host_ip = this.label.substring(6);
			host_ip = host_ip.substring(0, host_ip.indexOf('/'));
			String[] arr = host_ip.split(":");
			if(arr.length == 2) return arr;
		}
		return null;
	}
	
	/**
	 * Store the local variable to the cloud. 
	* priority:
	* 1. csd://ip_address/var_name
	* 2. useCloudConfig
	* 3. globalCloudConfig
	 * TODO change name to store()
	 */
	public boolean push() {
		String[] host_ip = parseName(this.getFullName());
		if(host_ip != null) {
			CloudClient c = new CloudClient(host_ip[0], Integer.valueOf(host_ip[1]));
			try {
				c.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return push(c);
		}
		CloudClient client = getCloudConfig().getCurrentClient();
		if(!getCloudConfig().isLocalConfig()) {
			return push(client);
		} else {
			this.isOnCloud = false;
		}
		return this.isOnCloud;
	}
	
	private boolean push(CloudClient client) {
		CloudSDRespHandler handler = client.getCloudSDRespHandler();
		try {
			System.err.println("Pushing data: "+this.toString()+" to "+client.toString());
			client.getChannel().writeAndFlush(this).sync();
			CloudSDResp resp = handler.getCloudResp();
			if(resp.status == 0)
				this.isOnCloud = true;
			else
				this.isOnCloud = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.isOnCloud;
	}
	
	/**
	 * Fetch a cloud variable to local. The name of the variable 
	 * on the cloud must be specified. Return true if success.
	 * Call getData() to access the data in the cloud variable
	 * TODO change name to fetch()
	 * @return
	 */
	public boolean fetch() {
		String[] host_ip = parseName(this.getFullName());
		if(host_ip != null && host_ip.length == 2) {
			CloudClient c = new CloudClient(host_ip[0], Integer.valueOf(host_ip[1]));
			try {
				c.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.err.println("Fetching data "+this.getFullName());
			return fetch(c);
		}
		if(getCloudConfig().isLocalConfig()) {
			synchronized(this) {
				while(!isReady) { //Return immediately if it is ready
					//Block until it is ready
					try {
						System.out.println("Fetching: "+this.toString());
						wait();
						System.out.println("Fetched: "+this.toString());
						return true;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if(isAsync) //only for test purpose
				System.out.println("Fetched without waiting: "+this.toString());
			return true;
		} else { 
			CloudClient client = getCloudConfig().getCurrentClient();
			return fetch(client);
		}
	}
	
	private boolean fetch(CloudClient client) {
		Channel ch = client.getChannel();
		CloudQuery qry = new CloudQuery();
		qry.objName = this.getFullName();
		qry.qryType = CloudQuery.CLOUD_SD;
		try {
			ch.writeAndFlush(qry).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CloudSDHandler h = client.getCloudSDHandler();
		
		//while(true) {
			CloudSD var = h.getCloudSD();
			this.data = var.data;
			this.isOnCloud = var.isOnCloud();
			if(this.data.length > 0)
				return this.isOnCloud;
			return false;
		//}
	}
	
	public boolean isOnCloud() {
		return isOnCloud;
	}
	
	public void setOnCloudFlag(boolean flag) {
		this.isOnCloud = flag;
	}
	
	@Override
	public Expr simplify() {
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		return null;
	}
	
	public static CloudSD valueOf(Expr expr) {
		return new CloudSD(expr);
	}
	
	public Expr assign(Expr expr) {
		return new LCAssign(this, expr);
	}
	
	public Expr assign(double val) {
		return new LCAssign(this, Expr.valueOf(val));
	}

	public Expr assign(int val) {
		return new LCAssign(this, Expr.valueOf(val));
	}
	
	public String toString() {
		if(isOnCloud)
			return this.getFullName()+" = "+printData() + " (On Cloud)";
		else
			return this.getFullName()+" = "+printData();
	}
	
	private String printData() {
		if(data == null || data.length == 0)
			return "[]";
		int max = 10;
		if(data.length < max) max = data.length;
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(data[0]);
		for(int i=1; i<max; i++) {
			sb.append(", ").append(data[i]);
		}
		if(data.length > max)
			sb.append(", ...]; len="+data.length);
		else
			sb.append("]");
		return sb.toString();
	}
	
	public void setIsReady(boolean flag) {
		isReady = flag;
		if(!isReady) isAsync = true;
	}

	/**
	 * Return true if the name is a generated name other than
	 * a user specified name.
	 * <BR>
	 * The generated name of the return value of a function will 
	 * always be replaced by the generated name from a cloud server
	 * 
	 * @return
	 */
	public boolean isGenName() {
		return this.isGenName;
	}
	
	private CloudSD compile(String name, Expr expr) {
		if(getCloudConfig().isLocalConfig()) {
			CloudSD[] args = Utils.extractCloudSDs(expr).toArray(new CloudSD[0]);
			BytecodeVecFunc fexpr = JIT.compileVecFunc(args, expr);
			data = new double[args[0].size()];
			fexpr.apply(data, 0, Utils.getDataFromCloudSDs(args));
		} else {
			//expr contains server references
			//TODO Is it possible to lazy eval the expr at cloud side?
			// that is to say this CloudSD is the return value of a CloudFunc
			// which constructed from the given expr?
			
			
			
		}
		return this;
	}
	
}

