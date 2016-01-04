package lambdacloud.core;

import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lambdacloud.core.lang.LCBase;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCVar;
import lambdacloud.net.CloudClient;
import lambdacloud.net.CloudFuncHandler;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudResp;
import lambdacloud.net.CloudVarHandler;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.bytecode.IR;
import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.JIT;

public class CloudFunc extends LCBase {
	public static enum FUNC_TYPE { SCALAR, VECTOR, BATCH }
	protected String name;
	protected BytecodeFunc func;
	protected BytecodeVecFunc vecFunc;
	protected BytecodeBatchFunc batchFunc;
	protected boolean isOnCloud = false;
	
	protected Class<?> clazz; //a java class of func
	protected Method method;
	
	protected CloudConfig localConfig = null;
	protected boolean isAsync = false;
	
	//1=BytecodeFunc, 2=BytecodeVecFunc, 3=BytecodeBatchFunc
	protected FUNC_TYPE funcType; 
	protected IR funcIR = null;
	
	public CloudFunc(String name) {
		this.name = name;
	}
	
	public CloudFunc(String name, Expr[] args, Expr expr) {
		this.name = name;
		this.compile(args, expr);
	}
	
	public CloudFunc(String name, Expr[] args, Expr[] expr) {
		this.name = name;
		this.compile(args, expr);
	}
	
	public CloudFunc(Expr[] args, Expr expr) {
		this.name = generateName();
		this.compile(args, expr);
	}
	
	public CloudFunc(LCVar[] args, Expr[] expr) {
		this.name = generateName();
		this.compile(args, expr);
	}
	
	public CloudFunc(CloudConfig config, String name) {
		this.name = name;
		this.localConfig = config;
	}
	
	public CloudFunc(CloudConfig config, String name, LCVar[] args, Expr expr) {
		this.name = name;
		this.localConfig = config;

		this.compile(args, expr);
	}
	
	public CloudFunc(CloudConfig config, String name, LCVar[] args, Expr[] expr) {
		this.name = name;
		this.localConfig = config;

		this.compile(args, expr);
	}
	
	public CloudFunc(CloudConfig config, LCVar[] args, Expr expr) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(args, expr);
	}
	
	public CloudFunc(CloudConfig config, LCVar[] args, Expr[] expr) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(args, expr);
	}
	
	private static String generateName() {
		return "CloudFunc"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * Use the given cloud configuration other than the global one
	 * @param conf
	 */
	public void useCloudConfig(CloudConfig conf) {
		this.localConfig = conf;
	}
	
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
	
	public CloudFunc(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.clazz = clazz;

		if(currentCloudConfig().isLocal()) {
			try {
				method = clazz.getMethod("apply", new Class[] {double[].class});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String filePath = System.getProperty("user.dir")+"/"+clazz.getName().replace(".", "/")+".class";
			File f = new File(filePath);
			if(!f.exists()) {
				filePath = System.getProperty("user.dir")+"/bin/"+clazz.getName().replace(".", "/")+".class";
				f = new File(filePath);
				if(!f.exists()) {
					filePath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath()+
							clazz.getName().replace(".", "/")+".class";
				}
			}
			Path path = Paths.get(filePath);
			try {
				byte[] data = Files.readAllBytes(path);
				IR ir =  new IR();
				ir.type = 1;
				ir.name = clazz.getName();
				ir.bytes = data;
				this.funcIR = ir;
				CloudFuncHandler handler =currentCloudConfig().currentClient().getCloudFuncHandler();
				Channel ch = currentCloudConfig().currentClient().getChannel();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public CloudFunc compile(Expr[] args, Expr expr) {
		Expr compileExpr = expr;
		//TODO Do we need LCReturn? It depends on how the compile function treat the return value of an expr
		if(!(expr instanceof LCBase)) {
			compileExpr = new LCReturn(expr);
		}
		
		
		if(currentCloudConfig().isLocal()) {
			funcType = FUNC_TYPE.SCALAR;
			func = CompileUtils.compile(name, compileExpr, args);
			//func = JIT.compile(args, expr);
			
		} else {
			//send the exprssion to the server
			funcIR = CompileUtils.getIR(name, compileExpr, args);
			

			//funcIR = JIT.getIR(name, args, expr);
			CloudFuncHandler handler = currentCloudConfig().currentClient().getCloudFuncHandler();
			Channel ch = currentCloudConfig().currentClient().getChannel();
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
	
	public CloudFunc compile(Expr[] args, Expr[] exprs) {
		if(currentCloudConfig().isLocal()) {
			funcType = FUNC_TYPE.VECTOR;
			vecFunc = JIT.compile(args, exprs);
		} else {
			//send the exprssion to the server
		}
		return this;
	}

	/**
	 * flag=true: run apply() method asynchronously
	 * flag=false: run apply() method synchronously
	 * 
	 * @param flag
	 */
	public void isAsyncApply(boolean flag) {
		this.isAsync = flag;
	}
	
	public void apply(CloudSD output, CloudSD ...inputs) {
		output.useCloudConfig(currentCloudConfig());
		for(int i=0; i<inputs.length; i++) 
			inputs[i].useCloudConfig(currentCloudConfig());
		
		if(this.clazz != null) {
			if(currentCloudConfig().isLocal()) {
				try {
					Object ret = method.invoke(this.clazz.newInstance(), inputs[0].getData());
					output.resize(1);
					output.setData(0, ((Double)ret).doubleValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
		if(currentCloudConfig().isLocal()) {
			if(this.clazz != null) {
				Double val1;
				try {
					val1 = (Double)method.invoke(clazz.newInstance(), inputs[0].getData());
					output.resize(1);
					output.setData(0, val1.doubleValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(inputs.length == 0) {
				switch(funcType) {
				case SCALAR:
					output.resize(1);
					output.setData(0, func.apply());
					break;
				case VECTOR:
					break;
				case BATCH:
					break;
				default:
					throw new RuntimeException();
				}
			} else if(inputs.length == 1) {
				double[] data = null;
				double d;
				switch(funcType) {
				case SCALAR:
					data = inputs[0].getData();
					d = func.apply(data);
					output.resize(1);
					output.setData(0, d);
					break;
				case VECTOR:
					data = inputs[0].getData();
					double[] out = output.getData();
					vecFunc.apply(out, 0, data);
					break;
				case BATCH:
					break;
				default:
					throw new RuntimeException();
				}
			} else {
				
			}
		} else {
			//Store argument on cloud first if necessary
			for(int i=0; i<inputs.length; i++) 
				if(!inputs[i].isOnCloud()) {
					inputs[i].storeToCloud();
				}
			
			CloudClient client = currentCloudConfig().currentClient();
			CloudVarHandler handler = client.getCloudVarHandler();
			try {
				CloudQuery qry = new CloudQuery();
				qry.qryType = CloudQuery.CLOUD_FUNC_EVAL;
				qry.objName = name;
				for(int i=0; i<inputs.length; i++) 
					qry.argNames.add(inputs[i].getLabel());
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

	@Override
	public TypeInfo getTypeInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public FUNC_TYPE getFuncType() {
		return this.funcType;
	}
	
	public void setFuncType(FUNC_TYPE type) {
		this.funcType = type;
	}
	
	/**
	 * For net work transfer of function type
	 * @param type
	 */
	public void setFuncType(int type) {
		switch(type) {
		case 1:
			this.funcType = FUNC_TYPE.SCALAR;
			break;
		case 2:
			this.funcType = FUNC_TYPE.VECTOR;
			break;
		case 3:
			this.funcType = FUNC_TYPE.BATCH;
			break;
		default:
			throw new RuntimeException("");
		}
	}
}
