package io.lambdacloud.core.utils;

import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.symjava.bytecode.BytecodeBatchFunc;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.bytecode.BytecodeVecFunc;

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
		
		switch(func.getFuncType()) {
		case SCALAR:
			BytecodeFunc bfunc = func.getBytecodeFunc();
			if(doubleArgs.length > 0) {
				//Support multiple CloudSD inputs for BytecodeFunc
				output.init(bfunc.apply(Utils.flatten(doubleArgs))); //Time consuming part
			} else {
				output.init(bfunc.apply());
			}
			break;
		case VECTOR:
			BytecodeVecFunc bfunc2 = func.getBytecodeVecFunc();
			//The length of returned array can be obtained from CloundFunc 
			double[] outAry = new double[func.getOutAryLen()];
			if(args.length > 0) {
				bfunc2.apply(outAry, 0, doubleArgs);
			} else
				bfunc2.apply(outAry, 0);
			output.init(outAry);
			break;
		case BATCH:
			BytecodeBatchFunc bfunc3 = func.getBytecodeBatchFunc();
			//The length of returned array can be obtained from CloundFunc 
			double[] outAry2 = new double[args[0].length()];
			if(args.length > 0) {
				bfunc3.apply(outAry2, 0, Utils.flatten(doubleArgs));
			} else
				bfunc3.apply(outAry2, 0);
			output.init(outAry2);
			break;
		default:
			throw new UnsupportedOperationException();
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
