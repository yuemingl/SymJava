package symjava.symbolic.utils;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Symbol;

public class JIT {
	
	private JIT() {}
	
	public static BytecodeFunc compile(Expr[] args, Expr expr) {
		if(expr instanceof Func) {
			Func func = (Func)expr;
			return func.toBytecodeFunc();
		} else {
			Func func = new Func("JITFunc_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), expr);
			func.args = args;
			return func.toBytecodeFunc(true, false);
		}
	}
	
	public static BytecodeFunc compile(Expr expr) {
		if(expr instanceof Func) {
			Func func = (Func)expr;
			return func.toBytecodeFunc();
		} else {
			Func func = new Func("JITFunc_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), expr);
			return func.toBytecodeFunc(true, false);
		}
	}
	
	public static BytecodeVecFunc compile(Expr[] args, Expr[] exprs) {
		boolean isWriteFile = true;
		boolean staticMethod = false;
		String className = "JITVecFunc_"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		try {
			FuncClassLoader<BytecodeVecFunc> fcl = new FuncClassLoader<BytecodeVecFunc>();
			ClassGen genClass = BytecodeUtils.genClassBytecodeVecFunc(className, exprs, args, isWriteFile, staticMethod);
			return fcl.newInstance(genClass);
			//return (BytecodeFunc)Class.forName("symjava.bytecode."+this.label).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Expr[] exprs = new Expr[3];
		exprs[0] = Symbol.x;
		exprs[1] = Symbol.x * Symbol.y;
		exprs[2] = Symbol.y + 1;
		BytecodeVecFunc vecFunc = compile(new Expr[]{Symbol.x, Symbol.y}, exprs);
		double[] outAry = new double[3];
		vecFunc.apply(outAry, 10,20);
		for(double d : outAry)
			System.out.println(d);
	}
}
