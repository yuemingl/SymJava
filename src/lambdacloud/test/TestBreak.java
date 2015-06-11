package lambdacloud.test;

import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCStatements;
import symjava.relational.Eq;
import symjava.relational.Lt;

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
