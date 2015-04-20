package lambdacloud.core;

import io.netty.channel.Channel;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudResp;
import lambdacloud.net.CloudVarHandler;
import lambdacloud.net.CloudVarRespHandler;
import lambdacloud.net.CloudVarResp;
import lambdacloud.net.CloudClient;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

public class CloudVar extends Symbol {
	double[] data = new double[0];
	boolean isOnCloud = false;
	
	public CloudVar() {
		super("CloudVar"+java.util.UUID.randomUUID().toString().replaceAll("-", ""));
	}

	public CloudVar(String name) {
		super(name);
	}
	
	public CloudVar(Expr expr) {
		super("CloudVar"+java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		this.compile(this.label, expr);
	}
	
	public CloudVar(String name, Expr expr) {
		super(name);
		this.compile(name, expr);
	}
	
	public CloudVar compile(String name, Expr expr) {
		if(CloudConfig.isLocal()) {
			CloudVar[] args = Utils.extractCloudVars(expr).toArray(new CloudVar[0]);
			BytecodeBatchFunc fexpr = JIT.compileBatchFunc(args, expr);
			data = new double[args[0].size()];
			fexpr.apply(data, 0, Utils.getDataFromCloudVars(args));
		} else {
			//expr contains server references
		}
		return this;
	}

	public CloudVar init(double ...array) {
		this.data = array;
		return this;
	}

	public void set(int index, double value) {
		data[index] = value;
	}
	
	public CloudVar resize(int size) {
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
	
	public String getName() {
		return this.label;
	}
	
	public double[] getData() {
		return data;
	}
	
	public void storeToCloud() {
		CloudClient client = CloudConfig.getClient();
		CloudVarRespHandler handler = client.getCloudVarRespHandler();
		try {
			client.getChannel().writeAndFlush(this).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CloudVarResp resp = handler.getCloudResp();
		if(resp.status == 0)
			this.isOnCloud = true;
		else
			this.isOnCloud = false;
	}
	
	/**
	 * Fetch a cloud variable to local. Return true if success. 
	 * Call getData() to access the data in the cloud variable
	 * @return
	 */
	public boolean fetchToLocal() {
		if(CloudConfig.isLocal())
			return true;
		else {
			CloudClient client = CloudConfig.getClient();
			Channel ch = client.getChannel();
			CloudQuery qry = new CloudQuery();
			qry.objName = this.getLabel();
			qry.qryType = CloudQuery.CLOUD_VAR;
			try {
				ch.writeAndFlush(qry).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CloudVarHandler h = client.getCloudVarHandler();
			CloudVar var = h.getCloudVar();
			this.data = var.data;
			this.isOnCloud = var.isOnCloud();
			return this.isOnCloud;
		}
	}
	
	public boolean isOnCloud() {
		return isOnCloud;
	}
	
	public void setOnCloudFlag(boolean flag) {
		this.isOnCloud = flag;
	}
	
	public int size() {
		return data.length;
	}
	
	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static CloudVar valueOf(Expr expr) {
		return new CloudVar(expr);
	}

}
