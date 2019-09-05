package io.lambdacloud.test;

import io.lambdacloud.core.lang.LCDouble;
import io.lambdacloud.core.lang.LCInt;
import io.lambdacloud.core.lang.LCLoop;
import io.lambdacloud.core.lang.LCReturn;
import io.lambdacloud.core.lang.LCStatements;
import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.relational.Lt;

public class TestBreak {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LCStatements lcs = new LCStatements();
		LCInt i = new LCInt("i");
		LCDouble sum = new LCDouble("sum");
		
		//for(int i=0; i<100; i++);
		LCLoop loop = new LCLoop(
				i.assign(0),      //i = 0
				Lt.apply(i, 100), //i < 100
				i.assign(i + 1)   //i = i+1
			);
		loop.appendBody(sum.assign(sum+i));
		loop.breakIf(Eq.apply(i, 50));
		lcs.append(loop);
		lcs.append(new LCReturn(sum));
		System.out.println(lcs);
		System.out.println(CompileUtils.compile(lcs).apply());

	}

}
