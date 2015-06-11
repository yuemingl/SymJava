package lambdacloud.test;

import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCReturn;
import symjava.relational.Eq;
import symjava.relational.Lt;

public class TestBreak {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LCInt i = new LCInt("i");

		//for(int i=0; i<100; i++);
		LCLoop loop = new LCLoop(
				i.assign(0),      //i = 0
				Lt.apply(i, 100), //i < 100
				i.assign(i + 1)   //i = i+1
			);
		loop.breakIf(Eq.apply(i, 50));
		loop.appendBody(new LCReturn(i));
		System.out.println(loop);
		System.out.println(CompileUtils.compile(loop).apply());

	}

}
