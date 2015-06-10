package lambdacloud.test;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.HashMap;

import lambdacloud.core.lang.LCIf;
import symjava.bytecode.BytecodeFunc;
import symjava.relational.Gt;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.FuncClassLoader;

import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

import static symjava.symbolic.Symbol.*;

public class TestBytecodeGen {
	public static BytecodeFunc gen(Expr[] args, Expr expr) {
		String packageName = "symjava.bytecode";
		String clsName = "TestBytecodeGen";
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
		
		//double sum = 0;
		LocalVariableGen lg;

		HashMap<String, Integer> argsMap = new HashMap<String, Integer>();
		for(int i=0; i<args.length; i++) {
			argsMap.put(args[i].getLabel(), i);
		}
		
		expr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, 1, null);

		//il.append(InstructionConstants.DCONST_0);
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
		return fcl.newInstance(cg);
	}
	
	public static void test1() {
		/**
		 double apply(double[] args) {
			 if(args[0] > args[1])
			 	return args[0];
			 else
			 	return args[1];
		 }
		 */
		LCIf cif = new LCIf(Gt.apply(x, y)); //x>y
		cif.appendTrue(x);
		cif.appendFalse(y);
		BytecodeFunc func = gen(new Expr[]{x, y}, cif);
		System.out.println(func.apply(new double[]{1,2}));
		System.out.println(func.apply(new double[]{10,2}));
	}
	
	public static void test2() {
		/**
		 double apply(double[] args) {
			 if(args[0] > args[1])
			 	return args[0];
			 else
			 	return args[1];
		 }
		 */
		LCIf cif = new LCIf(Gt.apply(x, y)); //x>y
		cif.appendTrue(x+y*x-x/y);
		cif.appendFalse(-x);
		BytecodeFunc func = gen(new Expr[]{x, y}, cif);
		System.out.println(func.apply(new double[]{1,2}));
		System.out.println(func.apply(new double[]{10,2}));
	}
	
	public static void main(String[] args) {
		test1();
		test2();
	}
}
