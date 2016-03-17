package lambdacloud.core.utils;

import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.bytecode.BytecodeFunc;

public class FuncEvalThread implements Runnable {
	CloudFunc func;
	CloudSD output;
	CloudSD[] args;
	
	public FuncEvalThread(CloudFunc func, CloudSD output, CloudSD... args) {
		this.func = func;
		this.output = output;
		this.args = args;
	}
	
	@Override
	public void run() {
		double[][] doubleArgs = new double[args.length][];
		for(int i=0; i<args.length; i++) {
			CloudSD v = args[i];
			v.fetch(); //block if not ready
			doubleArgs[i] = v.getData();
		}
		System.out.println("\t>>>"+Thread.currentThread().getName()+" evaluating "+func.getName()+"...; Return: "+output);
		BytecodeFunc bfunc = func.getBytecodeFunc();
		if(args.length > 0) {
			//Support multiple CloudSD inputs for BytecodeFunc
			output.init(bfunc.apply(Utils.flatten(doubleArgs))); //Time consuming part
		} else {
			output.init(bfunc.apply());
		}
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		synchronized(output) {
			output.setIsReady(true);
			output.notify();
		}
		
	}

}
