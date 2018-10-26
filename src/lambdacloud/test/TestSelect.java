package lambdacloud.test;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Table;
import symjava.symbolic.utils.JIT;
import static symjava.symbolic.Symbol.*;

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
