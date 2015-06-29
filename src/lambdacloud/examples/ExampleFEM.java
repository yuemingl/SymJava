package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCVar;
import symjava.bytecode.BytecodeFuncImpFEM;

public class ExampleFEM {
	
	
	public static void main(String[] args) {
		System.out.println("Current working dir="+System.getProperty("user.dir"));
		
		String configFile = "local";
		int nData = 1000;
		int nEle = 100000;
		boolean isAsync = false;
		if(args.length < 3) {
			System.out.println("args: configFile nData nEle isAsync");
		} else {
			if(args.length >= 3) {
				configFile = args[0];
				nData = Integer.valueOf(args[1]);
				nEle = Integer.valueOf(args[2]);
			}
			if(args.length == 4) {
				isAsync = Boolean.valueOf(args[3]);
			}
		}
		
		CloudConfig.setGlobalTarget(configFile);
		
		double[] data = new double[nData];
		for(int i=0; i<data.length; i++)
			data[i] = i;
		data[0] = nEle;
		
		CloudSD input = new CloudSD("input").init(data);
		CloudSD output = new CloudSD("output").resize(1);
		
		long start, end, totalTime;
		long start2, end2, applyTime;
		start = System.currentTimeMillis();
		start2 = System.currentTimeMillis();
		for(int i=0; i<CloudConfig.getGlobalConfig().getTotalNumClients(); i++) {
			CloudFunc f = new CloudFunc(BytecodeFuncImpFEM.class);
			f.isAsyncApply(isAsync);
			f.apply(output, input);
		}
		end2 = System.currentTimeMillis();
		applyTime = end2 - start2;

		for(int i=0; i<CloudConfig.getGlobalConfig().getTotalNumClients(); i++) {
			if(output.fetchToLocal()) {
				for(double d : output.getData()) {
					System.out.println("output="+d);
				}
			}
		}
		end = System.currentTimeMillis();
		totalTime = end-start;
		System.out.println("apply time="+applyTime+" getDataTime="+(totalTime-applyTime)+" totalTime="+totalTime);
	}
}
