package lambdacloud.core;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

public class CloudVar extends Symbol {
	double[] data;
	
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
	
	public double[] getAll() {
		return data;
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
	
	public void storeToCloud() {
		
	}
	
	public double[] fetchToLocal() {
		return data;
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
