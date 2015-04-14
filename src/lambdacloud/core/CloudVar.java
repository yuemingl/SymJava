package lambdacloud.core;

import java.util.ArrayList;
import java.util.List;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.SymDouble;
import symjava.symbolic.SymFloat;
import symjava.symbolic.SymInteger;
import symjava.symbolic.SymLong;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.JIT;

public class CloudVar extends Expr {
	CloudConfig config;
	double[] data;
	
	public CloudVar(CloudConfig config, String name) {
		this.label = name;
		this.sortKey = this.label;
		this.config = config;
	}
	
	public CloudVar(CloudConfig config, Expr expr) {
		if(config.isLocal()) {
			BytecodeBatchFunc fexpr = JIT.compileBatchFunc(new Expr[0], expr);
			fexpr.apply(data, 0);
		} else {
			//expr contains server references
		}
			
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
	
	public void storeToCloud() {
		
	}
	
	public double[] fetchToLocal() {
		return data;
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
	
	
	public static CloudVar valueOf(String s) {
		return null;
	}
	
	public static CloudVar valueOf(Expr expr) {
		List<Expr> outList = new ArrayList<Expr>();
		BytecodeUtils.post_order(expr, outList);
		CloudConfig config = null;
		for(Expr e : outList) {
			if(e instanceof CloudVar)
				config = ((CloudVar)e).getConfig();
		}
		CloudVar v = new CloudVar(config, expr);
		return v;
	}
	
	public static CloudVar valueOf(int v) {
		return new SymInteger(v);
	}
	
	public static CloudVar valueOf(long v) {
		return new SymLong(v);
	}
	
	public static CloudVar valueOf(float v) {
		return new SymFloat(v);
	}
	
	public static CloudVar valueOf(double v) {
		return new SymDouble(v);
	}
	public CloudConfig getConfig() {
		return this.config;
	}
}
