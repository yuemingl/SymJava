package symjava.examples;

import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.*;

/**
 * This example uses Java Operator Overloading for symbolic computation. 
 * See https://github.com/amelentev/java-oo for Java Operator Overloading.
 * 
 */
public class Example1 {

	public static void main(String[] args) {
		Expr expr = x + y * z;
		System.out.println(expr);
		
		Expr expr2 = expr.subs(x, y*y);
		System.out.println(expr2);
		System.out.println(expr2.diff(y));
		
		Func f = new Func("f1", expr2.diff(y));
		System.out.println(f);
		
		BytecodeFunc func = f.toBytecodeFunc();
		System.out.println(func.apply(1,2));
	}
}
