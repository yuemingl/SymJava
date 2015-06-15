package lambdacloud.core;

import io.netty.channel.Channel;
import lambdacloud.core.lang.LCBase;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCStatements;
import lambdacloud.core.lang.LCVar;
import lambdacloud.net.CloudFuncHandler;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudResp;
import lambdacloud.net.CloudVarHandler;
import lambdacloud.net.CloudClient;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.bytecode.IR;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class CloudFunc extends LCBase {
	String name;
	BytecodeFunc func;
	BytecodeVecFunc vecFunc;
	BytecodeBatchFunc batchFunc;
	boolean isOnCloud = false;
	
	//1=BytecodeFunc,2=BytecodeVecFunc,3=BytecodeBatchFunc
	int funcType = 0; 
	IR funcIR = null;
	
	public CloudFunc(String name) {
		this.name = name;
	}
	public CloudFunc(String name, LCVar[] args, Expr expr) {
		this.name = name;
		this.compile(name, args, expr);
	}
	public CloudFunc(String name, LCVar[] args, Expr[] expr) {
		this.name = name;
		this.compile(name, args, expr);
	}
	public CloudFunc(LCVar[] args, Expr expr) {
		this.name = "CloudFunc"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		this.compile(name, args, expr);
	}
	public CloudFunc(LCVar[] args, Expr[] expr) {
		this.name = "CloudFunc"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		this.compile(name, args, expr);
	}
	
	public CloudFunc compile(String name, LCVar[] args, Expr expr) {
		Expr compileExpr = expr;
		if(!(expr instanceof LCBase)) {
			compileExpr = new LCReturn(expr);
		}
		if(CloudConfig.isLocal()) {
			funcType = 1;
			func = CompileUtils.compile(compileExpr, args);
			//func = JIT.compile(args, expr);
			
		} else {
			//send the exprssion to the server
			funcIR = CompileUtils.getIR(compileExpr, args);
			//funcIR = JIT.getIR(name, args, expr);
			CloudFuncHandler handler = CloudConfig.getClient().getCloudFuncHandler();
			Channel ch = CloudConfig.getClient().getChannel();
			try {
				ch.writeAndFlush(this).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CloudResp resp = handler.getCloudResp();
			if(resp.status == 0)
				this.isOnCloud = true;
			else
				this.isOnCloud = false;
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

	public void apply(CloudSD output, CloudSD ...inputs) {
		if(CloudConfig.isLocal()) {
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
					data = inputs[0].getData();
					d = func.apply(data);
					output.set(0, d);
					break;
				case 2:
					data = inputs[0].getData();
					double[] out = output.getData();
					vecFunc.apply(out, 0, data);
					break;
				case 3:
					break;
				default:
					throw new RuntimeException();
				}
			} else {
				
			}
		} else {
			if(!inputs[0].isOnCloud()) {
				inputs[0].storeToCloud();
			}
			CloudClient client = CloudConfig.getClient();
			CloudVarHandler handler = client.getCloudVarHandler();
			try {
				CloudQuery qry = new CloudQuery();
				qry.qryType = CloudQuery.CLOUD_FUNC_EVAL;
				qry.objName = this.getName();
				qry.argNames.add(inputs[0].getLabel());
				qry.outputName = output.getName();
				client.getChannel().writeAndFlush(qry).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CloudSD rlt = handler.getCloudVar();
			output.setLabel(rlt.getLabel());
			output.data = rlt.data;
			output.isOnCloud = rlt.isOnCloud;
		}
	}
	
	public boolean isOnCloud() {
		return this.isOnCloud;
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
	@Override
	public Expr[] args() {
		// TODO Auto-generated method stub
		return null;
	}
}
