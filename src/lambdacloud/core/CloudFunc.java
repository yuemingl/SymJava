package lambdacloud.core;

import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lambdacloud.core.lang.LCBase;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.utils.FuncEvalThread;
import lambdacloud.net.CloudClient;
import lambdacloud.net.CloudFuncHandler;
import lambdacloud.net.CloudQuery;
import lambdacloud.net.CloudResp;
import lambdacloud.net.CloudSDHandler;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.bytecode.IR;
import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.utils.Utils;

/**
 * Cloud Function (CloudFunc)
 * <br>
 * This class represents a function on cloud.
 * A function is defined by giving a symbolic expression and arguments (optional).
 *
 */
public class CloudFunc extends LCBase {
	
	/**
	 * Supported types of functions
	 * @see BytecodeFunc
	 * @see BytecodeVecFunc
	 * @see BytecodeBatchFunc
	 */
	public static enum FUNC_TYPE { 
		SCALAR, // BytecodeFunc
		VECTOR, // BytecodeVecFunc
		BATCH   // BytecodeBatchFunc
	}
	
	protected String name; //TODO use label?
	protected BytecodeFunc      func;
	protected BytecodeVecFunc   vecFunc;
	protected BytecodeBatchFunc batchFunc;

	private static AtomicInteger cfuncNameGenerator = new AtomicInteger(0);
	
	//Info for BytecodeBatchFunc
	protected int outAryLen;
	protected int numArgs; //

	protected boolean isOnCloud = false;
	
	protected Class<?> clazz; //a java class of func
	protected Method method;
	
	protected CloudConfig localConfig = null;
	protected boolean isAsync = false;
	
	//1=BytecodeFunc, 2=BytecodeVecFunc, 3=BytecodeBatchFunc
	protected FUNC_TYPE funcType; 
	protected IR funcIR = null;
	
	//Need improving
	private int device;
	
	
	/**
	 * Construct a CloudFunc by providing an expression.
	 * The arguments of the function is extracted automatically.
	 * 
	 * @param expr
	 */
	public CloudFunc(Expr expr) {
		this.name = generateName();
		this.compile(expr);
	}
	
	/**
	 * Construct a CloudFunc by providing an expression and the arguments.
	 * 
	 * @param expr
	 * @param args
	 */
	public CloudFunc(Expr expr, Expr... args) {
		this.name = generateName();
		this.compile(expr, args);
	}

	/**
	 * Construct a CloudFunc by providing a list of expressions.
	 * The arguments of the function is extracted automatically.
	 * 
	 * @param expr
	 * @param args
	 */
	public CloudFunc(Expr[] expr) {
		this.name = generateName();
		this.compile(expr);
	}
	
	/**
	 * Construct a CloudFunc by providing a list of expressions and the arguments.
	 * 
	 * @param expr
	 * @param args
	 */
	public CloudFunc(Expr[] expr, Expr... args) {
		this.name = generateName();
		this.compile(expr, args);
	}

	/**
	 * What purpose by providing only a name? Get an existing function from cloud?
	 * @param name
	 */
	public CloudFunc(String name) {
		this.name = name;
	}

	public CloudFunc(String name, Expr expr) {
		this.name = name;
		this.compile(expr);
	}
	
	public CloudFunc(String name, Expr expr, Expr... args) {
		this.name = name;
		this.compile(expr, args);
	}
	
	public CloudFunc(String name, Expr[] exprs) {
		this.name = name;
		this.compile(exprs);
	}
	
	public CloudFunc(String name, Expr[] exprs, Expr... args) {
		this.name = name;
		this.compile(exprs, args);
	}
	
	public CloudFunc(CloudConfig config, Expr expr) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(expr);
	}
	
	public CloudFunc(CloudConfig config, Expr expr, Expr... args) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(expr, args);
	}

	public CloudFunc(CloudConfig config, Expr[] expr) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(expr);
	}
	
	public CloudFunc(CloudConfig config, Expr[] expr, Expr... args) {
		this.name = generateName();
		this.localConfig = config;
		this.compile(expr, args);
	}

	public CloudFunc(CloudConfig config, String name) {
		this.name = name;
		this.localConfig = config;
	}
	
	public CloudFunc(CloudConfig config, String name, Expr expr) {
		this.name = name;
		this.localConfig = config;
		this.compile(expr);
	}

	public CloudFunc(CloudConfig config, String name, Expr expr, Expr... args) {
		this.name = name;
		this.localConfig = config;
		this.compile(expr, args);
	}
	
	public CloudFunc(CloudConfig config, String name, Expr[] expr) {
		this.name = name;
		this.localConfig = config;
		this.compile(expr);
	}
	
	public CloudFunc(CloudConfig config, String name, Expr[] expr, Expr... args) {
		this.name = name;
		this.localConfig = config;
		this.compile(expr, args);
	}
	
	/**
	 * Use the given cloud configuration other than the global one
	 * @param conf
	 */
	public void setCloudConfig(CloudConfig conf) {
		this.localConfig = conf;
	}
	
	/**
	 * Return current cloud configuration. Default is the global configuration.
	 * @return
	 */
	public CloudConfig getCloudConfig() {
		if(this.localConfig != null)
			return this.localConfig;
		return CloudConfig.getGlobalConfig();
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
		CloudConfig config = getCloudConfig();
		config.setCurrentClient(this.device);
		
		//Make sure the output has the same CloudConfig as CloudFunc
		output.setCloudConfig(config);
		
		//for input see the priority of cloud config
		//TODO need better implementation for the priority in CloudSD
		for(int i=0; i<inputs.length; i++) {
			inputs[i].setCloudConfig(config);
		}
		
		if(this.clazz != null) {
			if(config.isLocalConfig()) {
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
		if(config.isLocalConfig()) {
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
			if(this.device >= 0) {
				//Set the flag before starting evaluating thread
				output.setIsReady(false);
				//Start a thread to do evaluation, the result will be write back to output once 
				//it is done. Call the method output.fetch() will block until the data is ready
				new Thread(new FuncEvalThread(this, output, inputs)).start();
				return;
			}
			
			if(inputs.length == 0) {
				switch(funcType) {
				case SCALAR:
					output.resize(1);
					output.setData(0, func.apply());
					break;
				case VECTOR:
					throw new UnsupportedOperationException();
				case BATCH:
					throw new UnsupportedOperationException();
				default:
					throw new RuntimeException();
				}
			} else if(inputs.length == 1) {
				double[] data = null;
				double d;
				switch(funcType) {
				case SCALAR:
					//TODO Support multiple CloudSD inputs
					data = inputs[0].getData();
					d = func.apply(data);
					output.resize(1);
					output.setData(0, d);
					break;
				case VECTOR:
					throw new UnsupportedOperationException();
				case BATCH:
					data = inputs[0].getData();
					double[] out = output.getData();
					batchFunc.apply(out, 0, data);
					break;
				default:
					throw new RuntimeException();
				}
			} else {
				
			}
		} else {
			//Store argument on cloud first if necessary
			for(int i=0; i<inputs.length; i++) {
				if(!inputs[i].isOnCloud()) {
					inputs[i].push();
				}
			}
			
			CloudClient client = config.getCurrentClient();
			CloudSDHandler handler = client.getCloudSDHandler();
			try {
				CloudQuery qry = new CloudQuery();
				qry.qryType = CloudQuery.CLOUD_FUNC_EVAL;
				
				//Function name (without package name)
				qry.objName = name; 
				
				//Arguments list
				for(int i=0; i<inputs.length; i++) 
					qry.argNames.add(inputs[i].getFullName());
				
				//The server will return a generated name for the output with empty name
				if(output.isGenName())
					qry.outputName = "";
				else
					qry.outputName = output.getName();
				if(this.isAsync)
					client.getChannel().writeAndFlush(qry);
				else
					client.getChannel().writeAndFlush(qry).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!this.isAsync) {
				CloudSD rlt = handler.getCloudSD(); //blockqueue
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
	
	public String getFullName() {
		CloudConfig config = this.getCloudConfig();
		CloudClient client = config.getClientByIndex(this.device);
		if(client != null) {
			return "csf://"+client.host+":"+client.port+"/"+this.name;
		}
		return name;
	}
	
	public IR getFuncIR() {
		return this.funcIR;
	}
	
	public CloudFunc setBytecodeFunc(BytecodeFunc f) {
		this.func = f;
		return this;
	}
	public CloudFunc setBytecodeBatchFunc(BytecodeBatchFunc f) {
		this.batchFunc = f;
		return this;
	}
	public CloudFunc setBytecodeVecFunc(BytecodeVecFunc f) {
		this.vecFunc = f;
		return this;
	}
	
	public BytecodeFunc getBytecodeFunc() {
		return this.func;
	}
	public BytecodeBatchFunc getBytecodeBatchFunc() {
		return this.batchFunc;
	}
	public BytecodeVecFunc getBytecodeVecFunc() {
		return this.vecFunc;
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
	
	public int getOutAryLen() {
		return this.outAryLen;
	}
	
	public void setOutAryLen(int len) {
		this.outAryLen = len;
	}
	
	public int getNumArgs() {
		return this.numArgs;
	}
	
	public void setNumArgs(int num) {
		this.numArgs = num;
	}
	
	private String generateName() {
		return "cfunc"+cfuncNameGenerator.incrementAndGet();
	}
	
	/**
	 * Construct a CloudFunc by providing a class which implements interface BytecodeFunc
	 * This is an experimental constructor.
	 * 
	 * @param clazz
	 */
	public CloudFunc(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.clazz = clazz;

		if(getCloudConfig().isLocalConfig()) {
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
				ir.type = FUNC_TYPE.SCALAR;
				ir.name = clazz.getName();
				ir.bytes = data;
				this.funcIR = ir;
				CloudFuncHandler handler =getCloudConfig().getCurrentClient().getCloudFuncHandler();
				Channel ch = getCloudConfig().getCurrentClient().getChannel();
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

	private CloudFunc compile(Expr expr) {
		List<Expr> args = Utils.extractSymbols(expr);
		return compile(expr, args.toArray(new Expr[0]));
	}
	
	private CloudFunc compile(Expr expr, Expr[] args) {
		Expr compileExpr = expr;
		
		this.device = 0;
		if(null != expr.getDevice())
			this.device = Integer.parseInt(expr.getDevice().name);
		//System.out.println("CloudFunc: compile " + expr + ", send to device: " + expr.getDevice().name);
		
		CloudConfig config = getCloudConfig();
		config.setCurrentClient(config.getClientByIndex(this.device));

		//TODO Do we need LCReturn? It depends on how the compile function treat the return value of an expr
		if(!(expr instanceof LCBase)) {
			compileExpr = new LCReturn(expr);
		}
		
		if(config.isLocalConfig()) {
			//Reset flags to generate matrix, vector declaration in a new func
			CompileUtils.bytecodeGenResetAll(expr);
			
			if(expr.getType() == TYPE.MATRIX || expr.getType() == TYPE.VECTOR) {
				this.vecFunc = CompileUtils.compileVecFunc(name, compileExpr, args);
				this.funcType = FUNC_TYPE.VECTOR; //BytecodeVecFunc
				if(expr.getType() == TYPE.VECTOR)
					this.outAryLen = expr.getTypeInfo().dim[0];
				else if(expr.getType() == TYPE.MATRIX)
					this.outAryLen = expr.getTypeInfo().dim[0]*expr.getTypeInfo().dim[1];
				this.numArgs = args.length;
			} else {
				funcType = FUNC_TYPE.SCALAR;
				func = CompileUtils.compile(name, compileExpr, args);
				//func = JIT.compile(args, expr);
				
			}
		} else {
			//send the expression to the server
			this.funcIR = CompileUtils.getIR(name, compileExpr, args);
			this.funcType = funcIR.type;
			this.outAryLen = funcIR.outAryLen;
			this.numArgs = funcIR.numArgs;

			//funcIR = JIT.getIR(name, args, expr);
			CloudFuncHandler handler = config.getCurrentClient().getCloudFuncHandler();
			Channel ch = config.getCurrentClient().getChannel();
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
	
	private CloudFunc compile(Expr[] exprs) {
		List<Expr> args = Utils.extractSymbols(exprs);
		compile(args.toArray(new Expr[0]), exprs);
		return this;
	}
	
	private CloudFunc compile(Expr[] exprs, Expr[] args) {
		if(getCloudConfig().isLocalConfig()) {
			this.funcType = FUNC_TYPE.BATCH;
			this.outAryLen = -1; //Determined by input CloudSD
			this.numArgs = args.length;
			//both works:
			//this.batchFunc = JIT.compileBatchFunc(args, exprs);
			this.batchFunc = CompileUtils.compileBatchFunc(name, exprs, args);
		} else {
			//Need reconsider 
			//method 1: See CompileUtils.compileBatchFunc which follows the implementation of JIT.compileBatchFunc()
			//BytecodeBatchFunc func = CompileUtils.compileBatchFunc(name, exprs, args);
			//this.funcType = FUNC_TYPE.BATCH;
			//this.batchFunc = func;
			
			//method 2: use output for vecfunction (not work, return a BytecodeVecFunc)
//			int dim = exprs.length;
//			LCStatements lcs = new LCStatements();
//			LCArray outAry = new LCDoubleArray("outAry");
//			for(int i=0; i<dim; i++) {
//				lcs.append( outAry[i].assign(exprs[i]) );
//			}
//			BytecodeVecFunc func = CompileUtils.compileVecFunc(lcs, outAry, args);
//			this.batchFunc = JIT.compileBatchFunc(args, exprs);
//			
			
			//send the expression to the server
			this.funcIR = CompileUtils.getIR(name, exprs, args);
			this.funcType = funcIR.type;
			this.outAryLen = -1;
			this.numArgs = funcIR.numArgs;

			CloudConfig config = this.getCloudConfig();
			CloudFuncHandler handler = config.getCurrentClient().getCloudFuncHandler();
			Channel ch = config.getCurrentClient().getChannel();
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
	
	public String toString() {
		return name;
	}
 }
