package lambdacloud.core;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DADD;
import com.sun.org.apache.bcel.internal.generic.DALOAD;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

import io.netty.channel.Channel;
import lambdacloud.net.CloudFuncHandler;
import lambdacloud.net.CloudResp;
import symjava.bytecode.BytecodeFunc;
import symjava.relational.Gt;
import symjava.relational.Lt;
import symjava.relational.Relation;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

public class CloudLoop extends CloudBase {
	Expr initExpr;
	Expr conditionExpr;
	Expr incrementExpr;
	List<Expr> bodyList = new ArrayList<Expr>();
	
	public CloudLoop(Expr conditionExpr) {
		this.conditionExpr = conditionExpr;
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
		this.incrementExpr = incrementExpr;
	}
	
	public CloudLoop appendBody(Expr expr) {
		bodyList.add(expr);
		return this;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(conditionExpr instanceof Relation))
			throw new RuntimeException();
		Relation cond = (Relation)conditionExpr;

		// Declare local variables
		List<Expr> vars = Utils.extractSymbols(args());
		for(Expr var : vars) {
			if(var instanceof CloudVar) {
				CloudVar cv = (CloudVar)var;
				int indexLVT = BytecodeUtils.declareLocal(cv, mg, il);
				cv.setLVTIndex(indexLVT);
			}
		}
		InstructionHandle loopStart = null;
		if(this.initExpr != null)
			loopStart = this.initExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		
		InstructionHandle bodyStart = null; //il.append(new NOP()); // Mark loop start position
		for(int i=0; i<bodyList.size(); i++) {
			Expr be = this.bodyList.get(i);
			InstructionHandle pos = be.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(bodyStart == null) bodyStart = pos;
		}
		if(this.incrementExpr != null) {
			InstructionHandle pos = this.incrementExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(bodyStart == null) bodyStart = pos;
		}

		InstructionHandle cmpStart = null; //il.append(new NOP()); // Mark comparison start position
		if(cond instanceof Lt) { // l < r
			cmpStart = cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(new IF_ICMPLT(bodyStart));
		} //else if (...)
		
		if(bodyStart != null)
			il.insert(bodyStart, new GOTO(cmpStart)); // goto comparison before the loop
		
		if(loopStart == null) loopStart = bodyStart;
		if(loopStart == null) loopStart = cmpStart;
		return loopStart;
	}
	
	public BytecodeFunc compile(Expr[] args) {
		String packageName = "symjava.bytecode";
		String clsName = "CloudLoop" + java.util.UUID.randomUUID().toString().replaceAll("-", "");
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
		
		HashMap<String, Integer> argsMap = new HashMap<String, Integer>();
		if(args != null) {
			for(int i=0; i<args.length; i++) {
				argsMap.put(args[i].getLabel(), i);
			}
		}
		this.bytecodeGen(fullClsName, mg, cp, factory, il, argsMap, 1, null);

		il.append(InstructionConstants.ILOAD_2);
		il.append(InstructionConstants.I2D);
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
	
	public void apply(CSD ...inputs) {
		
	}

	@Override
	public Expr[] args() {
		List<Expr> ret = new ArrayList<Expr>();
		ret.add(this.initExpr);
		ret.add(this.conditionExpr);
		ret.add(this.incrementExpr);
		ret.addAll(this.bodyList);
		return ret.toArray(new Expr[0]);
	}
}
