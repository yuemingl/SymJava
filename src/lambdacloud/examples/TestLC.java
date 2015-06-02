package lambdacloud.examples;

import lambdacloud.core.CloudLocalVar;
import lambdacloud.core.CloudLoop;
import lambdacloud.core.CloudVar;
import lambdacloud.core.LC;
import symjava.math.SymMath;
import symjava.relational.Lt;

public class TestLC {

	public static void main(String[] args) {
		LC cloudTask = new LC("server");
		
		CloudLocalVar x = cloudTask.localVar("x"), y = cloudTask.localVar("y");
		CloudVar output1 = cloudTask.globalVar("output1"), output2 = cloudTask.globalVar("output2");
		
		cloudTask.append(x.assign(3));
		cloudTask.append(y.assign(4));
		cloudTask.append(output1.assign(SymMath.sqrt(x*x+y*y)));

		CloudLocalVar i = cloudTask.localVar("i");
		CloudLocalVar sum = cloudTask.localVar("sum");
		
		//for(i=0; i<10; i++) {
		CloudLoop loop = cloudTask.forLoop(i.assign(0), Lt.apply(i, 10), i.assign(i+1));
		loop.appendBody(sum.assign(sum+i)); //sum = sum + i
		//} end for
		
		cloudTask.append(output2.assign(sum)); //output = sum
		
		cloudTask.run();
		
		output1.fetchToLocal();
		System.out.println(output1.get(0)); //=5
		
		output2.fetchToLocal();
		System.out.println(output2.get(0)); //=55
	}

}
