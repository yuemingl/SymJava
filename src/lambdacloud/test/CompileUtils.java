package lambdacloud.test;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.HashMap;
import java.util.List;

import lambdacloud.core.lang.LCVar;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.FuncClassLoader;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

public class CompileUtils {
	public static BytecodeFunc compile(Expr expr, Expr ...args) {
		String packageName = "symjava.bytecode";
		String clsName = expr.getClass().getSimpleName() + System.currentTimeMillis();
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
		System.out.println(fullClsName);
		StringBuilder sb = new StringBuilder();
		sb.append("double apply(");
		for(Expr a : args)
			sb.append("double ").append(a).append(",");
		if(args.length > 0)
			sb.delete(sb.length()-1, sb.length());
		sb.append(");");
		System.out.println(sb.toString());
		
		// Declare local variables
		List<Expr> vars = Utils.extractSymbols(expr);
		for(Expr var : vars) {
			if(var instanceof LCVar) {
				LCVar cv = (LCVar)var;
				int indexLVT = BytecodeUtils.declareLocal(cv, mg, il);
				cv.setLVTIndex(indexLVT);
			}
		}
		
		expr.bytecodeGen(fullClsName, mg, cp, factory, il, argsMap, 1, null);

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
