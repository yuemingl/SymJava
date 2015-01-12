package test;
import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_STATIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

public class HelloWorldBuilder {

	public static void main(String[] args) {
		// "(Ljava/lang/String;)Ljava/lang/StringBuffer;"
		Type.getMethodSignature(Type.STRINGBUFFER, new Type[] { Type.STRING });
		ClassGen cg = new ClassGen("BCELHelloWorld", "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, null);
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();

		MethodGen mg = new MethodGen(ACC_STATIC | ACC_PUBLIC, // access flags
				Type.VOID, // return type
				new Type[] { // argument types
						new ArrayType(Type.STRING, 1) 
					}, 
				new String[] { 
						"argv" 
					}, // arg
																			// names
				"main", "BCELHelloWorld", // method, class
				il, cp);
		InstructionFactory factory = new InstructionFactory(cg);

		ObjectType i_stream = new ObjectType("java.io.InputStream");
		ObjectType p_stream = new ObjectType("java.io.PrintStream");

		//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		il.append(factory.createNew("java.io.BufferedReader"));
		il.append(InstructionConstants.DUP); // Use predefined constant
		il.append(factory.createNew("java.io.InputStreamReader"));
		il.append(InstructionConstants.DUP);
		il.append(factory.createFieldAccess("java.lang.System", "in", i_stream,
				Constants.GETSTATIC));
		il.append(factory.createInvoke("java.io.InputStreamReader", "<init>",
				Type.VOID, new Type[] { i_stream }, Constants.INVOKESPECIAL));
		il.append(factory.createInvoke("java.io.BufferedReader", "<init>",
				Type.VOID, new Type[] { new ObjectType("java.io.Reader") },
				Constants.INVOKESPECIAL));
		LocalVariableGen lg = mg.addLocalVariable("in", new ObjectType(
				"java.io.BufferedReader"), null, null);
		int in = lg.getIndex();
		lg.setStart(il.append(new ASTORE(in))); // "in" valid from here
		
		//String name = null;
		lg = mg.addLocalVariable("name", Type.STRING, null, null);
		int name = lg.getIndex();
		il.append(InstructionConstants.ACONST_NULL);
		lg.setStart(il.append(new ASTORE(name))); // "name" valid from here
		
	    //  try {
	    //      System.out.print("Please enter your name> ");
	    //      name = in.readLine();
	    //    } catch(IOException e) { return; }
		InstructionHandle try_start = il.append(factory.createFieldAccess(
				"java.lang.System", "out", p_stream, Constants.GETSTATIC));

		il.append(new PUSH(cp, "Please enter your name> "));
		il.append(factory.createInvoke("java.io.PrintStream", "print",
				Type.VOID, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		
		il.append(new ALOAD(in));
		il.append(factory.createInvoke("java.io.BufferedReader", "readLine",
				Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
		il.append(new ASTORE(name));
		
		GOTO g = new GOTO(null);
		InstructionHandle try_end = il.append(g);
		InstructionHandle handler = il.append(InstructionConstants.RETURN);
		mg.addExceptionHandler(try_start, try_end, handler,
				new ObjectType("java.io.IOException"));
		InstructionHandle ih = il.append(factory.createFieldAccess(
				"java.lang.System", "out", p_stream, Constants.GETSTATIC));
		g.setTarget(ih);
		
		//System.out.println("Hello, " + name);
		il.append(factory.createNew(Type.STRINGBUFFER));
		il.append(InstructionConstants.DUP);
		il.append(new PUSH(cp, "Hello, "));
		il.append(factory.createInvoke("java.lang.StringBuffer", "<init>",
				Type.VOID, new Type[] { Type.STRING }, Constants.INVOKESPECIAL));
		il.append(new ALOAD(name));
		il.append(factory.createInvoke("java.lang.StringBuffer", "append",
				Type.STRINGBUFFER, new Type[] { Type.STRING },
				Constants.INVOKEVIRTUAL));
		il.append(factory.createInvoke("java.lang.StringBuffer", "toString",
				Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));

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
