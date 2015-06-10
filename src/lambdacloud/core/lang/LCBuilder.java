package lambdacloud.core.lang;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.CloudConfig;

import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.Utils;

/**
 * Lambda Cloud instruction builder
 *
 */
public class LCBuilder {
	CloudConfig config;
	List<Expr> stmts = new ArrayList<Expr>();

	public LCBuilder(CloudConfig config) {
		this.config = config;
	}
	
	public LCBuilder(String configFile) {
		//TODO change to not static
		CloudConfig.setTarget(configFile);
	}
	
	public LCLoop For(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		LCLoop cl = new LCLoop(initExpr, conditionExpr, incrementExpr);
		stmts.add(cl);
		return cl;
	}
	
	public LCLoop While(Expr conditionExpr) {
		LCLoop stmt = new LCLoop(conditionExpr);
		stmts.add(stmt);
		return stmt;
	}
	
	public LCIf If(Expr condition) {
		LCIf stmt = new LCIf(condition);
		stmts.add(stmt);
		return stmt;
	}
	
	public LCReturn Return(Expr expr) {
		LCReturn stmt = new LCReturn(expr);
		stmts.add(stmt);
		return stmt;
	}
	
	public LCBuilder append(Expr expr) {
		stmts.add(expr);
		return this;
	}
	
	public CloudSD declareCloudSD(String name) {
		return new CloudSD(name);
	}

	public LCVar declareInt(String name) {
		return new LCInt(name);
	}
	
	public LCVar declareLong(String name) {
		return new LCLong(name);
	}
	
	public LCVar declareFloat(String name) {
		return new LCFloat(name);
	}
	
	public LCVar declareDouble(String name) {
		return new LCDouble(name);
	}
	
	public LCVar declareShort(String name) {
		return new LCShort(name);
	}
	
	public LCVar declareChar(String name) {
		return new LCChar(name);
	}
	
	public LCVar declareByte(String name) {
		return new LCByte(name);
	}
	
	public CloudFunc build(Expr ...args) {
		CloudFunc func = new CloudFunc(args, new LCStatements(this.stmts));
		return func;
	}
	
	public BytecodeFunc compile(Expr ...args) {
		String packageName = "symjava.bytecode";
		String clsName = "LC" + java.util.UUID.randomUUID().toString().replaceAll("-", "");
		String fullClsName = packageName+"."+clsName;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"symjava.bytecode.BytecodeFunc"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);
		
		short acc_flags = ACC_PUBLIC;
		MethodGen mg = new MethodGen(acc_flags, // access flags
				Type.DOUBLE, // return type
				new Type[] { // argument types
					new ArrayType(Type.DOUBLE, 1) 
				}, 
				new String[] { "args" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		
		List<Expr> vars = Utils.extractSymbols(this.stmts.toArray(new Expr[0]));
		for(Expr var : vars) {
			if(var instanceof LCVar) {
				LCVar cv = (LCVar)var;
				int indexLVT = BytecodeUtils.declareLocal(cv, mg, il);
				cv.setLVTIndex(indexLVT);
			}
		}
		
		HashMap<String, Integer> argsMap = new HashMap<String, Integer>();
		if(args != null) {
			for(int i=0; i<args.length; i++) {
				argsMap.put(args[i].getLabel(), i);
			}
		}
		for(Expr expr : this.stmts) {
			expr.bytecodeGen(fullClsName, mg, cp, factory, il, argsMap, 1, null);
		}
		
		// Return 0 by default
		if(!(this.stmts.get(this.stmts.size()-1) instanceof LCReturn)) {
			il.append(InstructionConstants.DCONST_0);
			il.append(InstructionConstants.DRETURN);
		}
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		
		try {
			cg.getJavaClass().dump("bin/symjava/bytecode/"+clsName+".class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}
		FuncClassLoader<BytecodeFunc> fcl = new FuncClassLoader<BytecodeFunc>();
		BytecodeFunc fun = fcl.newInstance(cg);
		return fun;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : stmts) {
			sb.append(e).append("\n");
		}
		return sb.toString();
	}
}
