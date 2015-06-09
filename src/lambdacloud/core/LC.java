package lambdacloud.core;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.Utils;

/**
 * Lambda Cloud instruction builder
 *
 */
public class LC extends CloudBase {
	CloudConfig config;
	List<Expr> stmts = new ArrayList<Expr>();

	public LC(CloudConfig config) {
		this.config = config;
	}
	
	public LC(String configFile) {
		//TODO change to not static
		CloudConfig.setTarget(configFile);
	}
	
	public CloudLoop forLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		CloudLoop cl = new CloudLoop(initExpr, conditionExpr, incrementExpr);
		stmts.add(cl);
		return cl;
	}
	
	public CloudLoop whileLoop(Expr conditionExpr) {
		return new CloudLoop(conditionExpr);
	}
	
	public CloudIf If(Expr condition) {
		return new CloudIf(condition);
	}
	public LC append(Expr expr) {
		stmts.add(expr);
		return this;
	}
	
	public CSD declareCSD(String name) {
		return new CSD(name);
	}

	public CloudVar declareInt(String name) {
		return new CloudInt(name);
	}
	
	public CloudVar declareLong(String name) {
		return new CloudLong(name);
	}
	
	public CloudVar declareFloat(String name) {
		return new CloudFloat(name);
	}
	
	public CloudVar declareDouble(String name) {
		return new CloudDouble(name);
	}
	
	public CloudVar declareShort(String name) {
		return new CloudShort(name);
	}
	
	public CloudVar declareChar(String name) {
		return new CloudChar(name);
	}
	
	public CloudVar declareByte(String name) {
		return new CloudByte(name);
	}
	
	public void apply(CSD ...args) {
		//fun.apply(args);
	}
	
	public BytecodeFunc compile(Expr[] args) {
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
			if(var instanceof CloudVar) {
				CloudVar cv = (CloudVar)var;
				int indexLVT = declareLocal(cv, mg, il);
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
		
		il.append(InstructionConstants.DCONST_0);
		il.append(InstructionConstants.DRETURN);
		
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
}
