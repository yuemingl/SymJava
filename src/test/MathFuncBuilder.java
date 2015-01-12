package test;
import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_STATIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.lang.reflect.Method;

import bytecode.BytecodeFunc;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DADD;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DMUL;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;


public class MathFuncBuilder {
	public static void main(String[] args) {
		test2();
		
		// with reflection
		Class<?> c;
		try {
			c = Class.forName("genclass.MyFun1");
			Object fun = c.newInstance();
			Class<?>[] argTypes = new Class<?>[] { double.class, double.class };
			Method m = c.getDeclaredMethod("apply", argTypes);
			System.out.println(m.invoke(fun, 3, 4));
			
			BytecodeFunc func = (BytecodeFunc)fun;
			func.apply(3,4);
			
//		    Method[] allMethods = c.getDeclaredMethods();
//		    for (Method m : allMethods) {
//				String mname = m.getName();
//		 		java.lang.reflect.Type[] pType = m.getGenericParameterTypes();
//			    Double rlt = (Double)m.invoke(fun, 3.0, 4.0);
//			    System.out.println(mname+": "+rlt);
//		    }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test2() {
		String packageName = "genclass";
		String clsName = "MyFun1";
		String fullClsName = packageName+"."+clsName;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"genclass.Function_"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();

		MethodGen mg = new MethodGen(ACC_PUBLIC, // access flags
				Type.DOUBLE, // return type
				new Type[] { // argument types
					Type.DOUBLE, Type.DOUBLE 
				}, 
				new String[] { "x", "y" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		InstructionFactory factory = new InstructionFactory(cg);

		LocalVariableGen lg = null;
		lg = mg.addLocalVariable("rlt", Type.DOUBLE, null, null);
		int rlt = lg.getIndex();
		il.append(InstructionConstants.DCONST_0);
		lg.setStart(il.append(new DSTORE(rlt))); // "rlt" valid from here
		
		//Math.sqrt( x*x + y*y )
		il.append(new DLOAD(1));
		il.append(new DLOAD(1));
		il.append(new DMUL());
		il.append(new DLOAD(3));
		il.append(new DLOAD(3));
		il.append(new DMUL());
		il.append(new DADD());
		il.append(factory.createInvoke("java.lang.Math", "sqrt",
				Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));		
		il.append(new DSTORE(rlt));
		
		//System.out.println(rlt);
		il.append(factory.createFieldAccess(
				"java.lang.System", "out", new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
		il.append(new DLOAD(rlt));
		il.append(factory.createInvoke("java.lang.String", "valueOf",
				Type.STRING, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
		
		il.append(factory.createInvoke("java.io.PrintStream", "println",
				Type.VOID, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		
		//return
		il.append(new DLOAD(rlt));
		il.append(InstructionConstants.DRETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		try {
			cg.getJavaClass().dump("bin/genclass/"+clsName+".class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}		
	}
	
	
	public static void test() {
		ClassGen cg = new ClassGen("BCELHelloWorld", "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, null);
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();

		MethodGen mg = new MethodGen(ACC_STATIC | ACC_PUBLIC, // access flags
				Type.VOID, // return type
				new Type[] { // argument types
				new ArrayType(Type.STRING, 1) }, new String[] { "argv" }, // arg
																			// names
				"main", "BCELHelloWorld", // method, class
				il, cp);
		InstructionFactory factory = new InstructionFactory(cg);

		LocalVariableGen lg = null;
		lg = mg.addLocalVariable("name", Type.STRING, null, null);
		int name = lg.getIndex();
		il.append(InstructionConstants.ACONST_NULL);
		lg.setStart(il.append(new ASTORE(name))); // "name" valid from here
		
		//System.out.println("sqrt(2.0)=");
		il.append(factory.createFieldAccess(
				"java.lang.System", "out", new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
		il.append(new PUSH(cp, "sqrt(2.0)="));
		il.append(factory.createInvoke("java.io.PrintStream", "println",
		Type.VOID, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		
		//System.out.println(Math.sqrt(2.0));
		il.append(factory.createFieldAccess(
				"java.lang.System", "out", new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
		il.append(new PUSH(cp, 2.0));
		il.append(factory.createInvoke("java.lang.Math", "sqrt",
				Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
		
		il.append(factory.createInvoke("java.lang.String", "valueOf",
				Type.STRING, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
		
		il.append(factory.createInvoke("java.io.PrintStream", "println",
				Type.VOID, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		
		//return
		il.append(InstructionConstants.RETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		try {
			cg.getJavaClass().dump("BCELHelloWorld.class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}
	}

}
