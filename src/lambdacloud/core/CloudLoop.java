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
		return this;
	}
	
	public int declareLocal(CloudVar var, MethodGen mg, InstructionList il) {
		//variable name
		//initial value
		//index in local variable table (LVT)
		if(var instanceof CloudInt) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.INT, null, null);
			int idx = lg.getIndex();
			//il.append(InstructionConstants.ICONST_0);
			//lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudLong) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.LONG, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.LCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
			
		} else if(var instanceof CloudFloat) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.FLOAT, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.FCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudDouble) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.DOUBLE, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.DCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudBoolean) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.BOOLEAN, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudChar) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.CHAR, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudByte) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.BYTE, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudShort) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.SHORT, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		}
		throw new RuntimeException();
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
		List<Expr> allExprs = new ArrayList<Expr>();
		allExprs.add(initExpr);
		allExprs.add(conditionExpr);
		allExprs.add(incrementExpr);
		allExprs.addAll(bodyList);
		List<Expr> vars = Utils.extractSymbols(allExprs.toArray(new Expr[0]));
		for(Expr var : vars) {
			if(var instanceof CloudVar) {
				CloudVar cv = (CloudVar)var;
				int indexLVT = declareLocal(cv, mg, il);
				cv.setLVTIndex(indexLVT);
			}
		}
		
		if(this.initExpr != null)
			this.initExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		InstructionHandle loopStart = il.append(new NOP()); // Mark loop start position
		for(int i=0; i<bodyList.size(); i++) {
			Expr be = this.bodyList.get(i);
			be.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		}
		if(this.incrementExpr != null)
			this.incrementExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);

		InstructionHandle cmpStart = il.append(new NOP()); // Mark comparison start position
		if(cond instanceof Lt) { // l < r
			cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(new IF_ICMPLT(loopStart));
		} //else if (...)
		
		
		return il.insert(loopStart, new GOTO(cmpStart)); // goto comparison before the loop
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
	
	public void apply(CloudSharedVar ...inputs) {
		
	}
}
