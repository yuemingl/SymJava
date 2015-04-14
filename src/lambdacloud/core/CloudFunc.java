package lambdacloud.core;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class CloudFunc {
	CloudConfig config;
	BytecodeFunc func;
	BytecodeVecFunc vecFunc;
	BytecodeBatchFunc batchFunc;
	//1=BytecodeFunc,2=BytecodeVecFunc,3=BytecodeBatchFunc
	int funcType = 0; 
	
	public CloudFunc(CloudConfig config) {
		this.config = config;
	}
	
	public CloudFunc compile(Expr[] args, Expr expr) {
		if(config.isLocal()) {
			funcType = 1;
			func = JIT.compile(args, expr);
		} else {
			//send the exprssion to the server
		}
		return this;
	}
	
	public CloudFunc compile(Expr[] args, Expr[] expr) {
		if(config.isLocal()) {
			funcType = 2;
			vecFunc = JIT.compile(args, expr);
		} else {
			//send the exprssion to the server
		}
		return this;
	}

	public void apply(CloudVar output, CloudVar ...inputs) {
		if(inputs.length == 0) {
			switch(funcType) {
			case 1:
				output.set(0, func.apply());
				break;
			case 2:
				break;
			case 3:
				break;
			default:
				throw new RuntimeException();
			}
		} else if(inputs.length == 1) {
			double[] data = null;
			double d;
			switch(funcType) {
			case 1:
				data = inputs[0].fetchToLocal();
				d = func.apply(data);
				output.set(0, d);
				break;
			case 2:
				data = inputs[0].fetchToLocal();
				double[] out = output.fetchToLocal();
				vecFunc.apply(out, 0, data);
				break;
			case 3:
				break;
			default:
				throw new RuntimeException();
			}
		} else {
			
		}
	}
}
