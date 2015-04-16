package lambdacloud.core;

import io.netty.channel.Channel;
import lambdacloud.net.CloudFuncHandler;
import lambdacloud.net.CloudResp;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.bytecode.IR;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class CloudFunc {
	String name;
	BytecodeFunc func;
	BytecodeVecFunc vecFunc;
	BytecodeBatchFunc batchFunc;
	
	//1=BytecodeFunc,2=BytecodeVecFunc,3=BytecodeBatchFunc
	int funcType = 0; 
	IR funcIR = null;
	
	public CloudFunc(String name) {
		this.name = name;
	}
	public CloudFunc(String name, Expr[] args, Expr expr) {
		this.name = name;
		this.compile(name, args, expr);
	}
	public CloudFunc(String name, Expr[] args, Expr[] expr) {
		this.name = name;
		this.compile(name, args, expr);
	}
	public CloudFunc(Expr[] args, Expr expr) {
		this.name = "CloudFunc"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		this.compile(name, args, expr);
	}
	public CloudFunc(Expr[] args, Expr[] expr) {
		this.name = "CloudFunc"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		this.compile(name, args, expr);
	}
	
	public CloudFunc compile(String name, Expr[] args, Expr expr) {
		if(CloudConfig.isLocal()) {
			funcType = 1;
			func = JIT.compile(args, expr);
			
		} else {
			//send the exprssion to the server
			funcIR = JIT.getIR(args, expr);
			CloudFuncHandler handler = CloudConfig.getClient().getCloudFuncHandler();
//			handler.send(this);
			
			Channel ch = CloudConfig.getClient().getChnnel();
			try {
				ch.writeAndFlush(this).sync();
				// Wait until the connection is closed.
	            //ch.closeFuture().sync();
	            
				CloudResp resp = handler.getCloudResp();
				System.out.println(resp);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return this;
	}
	
	public CloudFunc compile(String name, Expr[] args, Expr[] exprs) {
		if(CloudConfig.isLocal()) {
			funcType = 2;
			vecFunc = JIT.compile(args, exprs);
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
	public String getName() {
		return this.name;
	}
	
	public IR getFuncIR() {
		return this.funcIR;
	}
	
	public CloudFunc setBytecodeFunc(BytecodeFunc f) {
		this.func = f;
		return this;
	}
	public CloudFunc setBytecodeVecFunc(BytecodeVecFunc f) {
		this.vecFunc = f;
		return this;
	}
	public CloudFunc setBytecodeBatchFunc(BytecodeBatchFunc f) {
		this.batchFunc = f;
		return this;
	}
	
	public BytecodeFunc getBytecodeFunc() {
		return this.func;
	}
	public BytecodeVecFunc getBytecodeVecFunc() {
		return this.vecFunc;
	}
	public BytecodeBatchFunc getBytecodeBatchFunc() {
		return this.batchFunc;
	}
}
