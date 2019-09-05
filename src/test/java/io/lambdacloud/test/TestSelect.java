package io.lambdacloud.test;

import io.lambdacloud.symjava.bytecode.BytecodeBatchFunc;
import io.lambdacloud.symjava.bytecode.BytecodeVecFunc;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Table;
import io.lambdacloud.symjava.symbolic.utils.JIT;
import static io.lambdacloud.symjava.symbolic.Symbol.*;

public class TestSelect {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Table t = new Table(x, y);
		//BytecodeBatchFunc f = JIT.compileBatchFunc(new Expr[]{x,y}, t.args());
		//JIT.compileBatchVecFunc()???
		/**
		 * For each column in select clause do the evaluation
		 */
		BytecodeVecFunc f2 = JIT.compileVecFunc(new Expr[]{x,y}, t.args());
		f.apply(outAry, 0, args);
	}
	


}
