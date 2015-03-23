package symjava.symbolic.utils;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;

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
}
