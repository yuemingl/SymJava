package lambdacloud.core;

import symjava.relational.Eq;
import symjava.symbolic.Expr;
import lambdacloud.net.CloudClient;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudVarHandler;

public class CloudLib {
	protected CloudConfig localConfig = null;
	protected boolean isAsync = false;
	/**
	 * Return current cloud configuration. Default is the global configuration.
	 * @return
	 */
	public CloudConfig currentCloudConfig() {
		if(this.localConfig != null)
			return this.localConfig;
		CloudConfig config = CloudConfig.getGlobalConfig();
		if(config == null) {
			throw new RuntimeException("CloudConfig is not specified!");
		}
		return config;
	}
	
	public static String convertParam(double[] param) {
		String ret = "[D:[";
		for(double d : param) 
			ret += String.valueOf(d)+",";
		return ret+"]";
	}
	
	public static String convertParam(double param) {
		return "D:"+String.valueOf(param);
	}
	
	public static String convertParam(int param) {
		return "I:"+String.valueOf(param);
	}
	
	public static String convertParam(Expr expr) {
		return "E:"+expr.toString();
	}
	public static String convertParam(Expr[] expr) {
		String ret = "[E:[";
		for(Expr e : expr) 
			ret += e.toString()+";";
		return ret+"]";
	}
	
	public void solverNewton(Eq[] eqs, double[] init, int maxIter, double eps, CloudSD output) {
		solverNewton(eqs, init, new double[0], maxIter, eps, output);
	}
	
	public void solverNewton(Eq[] eqs, double[] init, double[] params, int maxIter, double eps, CloudSD output) {
		String sEqs = convertParam(eqs);
		String sInit = convertParam(init);
		String sParam = convertParam(params);
		String sMaxIter = convertParam(maxIter);
		String sEps = convertParam(eps);
		
		invokeStatic("symjava.examples.Newton","solve", new String[]{sEqs, sInit, sParam, sMaxIter, sEps}, output);
	}
	
	
	protected void invokeStatic(String className, String methodName, String[] args, CloudSD output) {
		CloudClient client = currentCloudConfig().currentClient();
		CloudVarHandler handler = client.getCloudVarHandler();
		try {
			CloudQuery qry = new CloudQuery();
			qry.qryType = CloudQuery.CLOUD_LIB_INVOKE;
			qry.objName = className+"."+methodName;
			for(int i=0; i<args.length; i++) 
				qry.argNames.add(args[i]);
			qry.outputName = output.getName();
			if(this.isAsync)
				client.getChannel().writeAndFlush(qry);
			else
				client.getChannel().writeAndFlush(qry).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!this.isAsync) {
			CloudSD rlt = handler.getCloudVar(); //blockqueue
			output.setLabel(rlt.getLabel());
			output.data = rlt.data;
			output.isOnCloud = rlt.isOnCloud;
		}		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
