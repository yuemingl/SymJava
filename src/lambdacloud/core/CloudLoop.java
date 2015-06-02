package lambdacloud.core;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.ArrayList;
import java.util.List;

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
import symjava.symbolic.Expr;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.JIT;

public class CloudLoop extends CloudBase {
	Expr condition;
	CloudVar[] condArgs;
	
	List<Expr> bodyList = new ArrayList<Expr>();
	List<CloudVar[]> bodyArgs = new ArrayList<CloudVar[]>();
	
	public CloudLoop(Expr conditionExpr) {
		
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr) {
		
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		
	}
	
	public CloudLoop appendBody(Expr expr) {
		return this;
	}
	
	public CloudFunc compile() {
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
		
		///////////////////////////////////
		//>>>>define local variables in condition and bodyList
		//double sum = 0;
		LocalVariableGen lg;
		lg = mg.addLocalVariable("sum",
				Type.DOUBLE, null, null);
		int idxSum = lg.getIndex();
		il.append(InstructionConstants.DCONST_0);
		lg.setStart(il.append(new DSTORE(idxSum))); // "sum" valid from here

		//////////////////////////////////////////////////////////////
		//	for(int i=0; i<10; i++) {
		//		sum += args[i];
		//	}
		/////////////////////////////////////////////////////////////
		//int i = 0;
		lg = mg.addLocalVariable("i",
				Type.INT, null, null);
		int idxI = lg.getIndex();
		il.append(InstructionConstants.ICONST_0);
		lg.setStart(il.append(new ISTORE(idxI))); // "i" valid from here
		
		
		InstructionHandle loopStart = il.append(new NOP()); // Mark loop start position
		for(int i=0; i<bodyList.size(); i++) {
			Expr be = bodyList.get(i);
			Expr beArgs = bodyArgs.get(i);
			BytecodeUtils.addToInstructionList(mg, cp, factory, il, argsIndex, be, beArgs, argsMap);
			
		}
//		//Loop body: sum = sum + args[i]
//		InstructionHandle loopStart = il.append(new ALOAD(1));
//		il.append(new ILOAD(idxI));
//		il.append(new DALOAD());
//		il.append(new DLOAD(idxSum));
//		il.append(new DADD());
//		il.append(new DSTORE(idxSum));
		
//		//i++
//		il.append(new IINC(idxI, 1));

//		//Compare: i < 10
//		InstructionHandle loopCmp = il.append(new ILOAD(idxI));
//		il.append(new PUSH(cp, 10));
//		il.append(new IF_ICMPLT(loopStart));
		
		//Mark loop compare
		InstructionHandle loopCmp = il.append(new NOP());
		//condition must be one of Lt,LE,Gt,Ge,Eq,Neq
		BytecodeUtils.addOthers(mg, cp, factory, il, argsIndex, condition, args, argsMap);
		
		il.insert(loopStart, new GOTO(loopCmp));
		/////////////////////////////////////////////////////////////

		il.append(new DLOAD(idxSum));
		il.append(InstructionConstants.DRETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		FuncClassLoader<BytecodeFunc> fcl = new FuncClassLoader<BytecodeFunc>();
		BytecodeFunc fun = fcl.newInstance(cg);
		double[] params = new double[10];
		for(int i=0; i<params.length; i++)
			params[i] = i;
		System.out.println(fun.apply(params));
		
		try {
			cg.getJavaClass().dump("bin/symjava/bytecode/"+clsName+".class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}
	}
	
	public void apply(CloudVar ...inputs) {
		
	}
}
