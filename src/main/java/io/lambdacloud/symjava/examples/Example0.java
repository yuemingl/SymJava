package io.lambdacloud.symjava.examples;

import io.lambdacloud.symjava.symbolic.*;
import static io.lambdacloud.symjava.symbolic.Symbol.*;
import io.lambdacloud.symjava.symbolic.utils.JIT;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;

public class Example0 {
	public static void main(String[] args) {
		Expr R = 0.127-(x*0.194/(y+0.194));
		Expr Rdy = R.diff(y);
		System.out.println(Rdy);
		//Just-In-Time compile the symbolic expression to native code
		BytecodeFunc func = JIT.compile(new Expr[]{x,y}, Rdy);
		System.out.println(func.apply(0.362, 0.556)); //Scala function call operator
		System.out.println(func.call(0.362, 0.556)); //Groovy function call operator
		
	}
}
