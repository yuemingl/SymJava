package lambdacloud.test;

import static symjava.math.SymMath.log;
import static symjava.math.SymMath.random;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCIf;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCVar;
import symjava.relational.Ge;
import symjava.relational.Le;
import symjava.relational.Lt;
import symjava.symbolic.Expr;

public class TestLCBuilder {
	
	public static void testOverWriteArgs() {
		LCBuilder task = new LCBuilder("local");
		
		task.append(x.assign(sqrt(x*x+y*y))); //x=x+y
		System.out.println(task);
		
		double[] args = new double[]{3,4};
		task.compile(new Expr[]{x, y}).apply(args);
		
		
		System.out.println(args[0]); //5.0
	}
	
	public static void testReturn() {
		LCBuilder cloudTask = new LCBuilder("local");
		
		cloudTask.Return(sqrt(x*x+y*y)); //return sqrt(x*x+y*y)
		System.out.println(cloudTask);
		
		double[] args = new double[]{3,4};
		double ret = cloudTask.compile(new Expr[]{x, y}).apply(args);
		System.out.println(ret); //5.0
		
	}
	
	public static void testLoopAsignReturn() {
		LCBuilder cloudTask = new LCBuilder("local");
		
		LCVar i = cloudTask.declareInt("i"); //int i;
		LCVar sum = cloudTask.declareDouble("sum");//double sum;
		
		//for(i=0; i<10; i++) {
		cloudTask.For(i.assign(0), Lt.apply(i, 10), i.assign(i+1))
			.appendBody(sum.assign(sum+i)); //sum = sum + i
		//} //end for
		
		cloudTask.Return(sum); //return sum;
		System.out.println(cloudTask);
		
		System.out.println(cloudTask.compile().apply()); //45.0
	}

	public static void test() {
//		LC cloudTask = new LC("local");
//		CloudVar x = cloudTask.declareDouble("x");
//		CloudVar y = cloudTask.declareDouble("y");
//		CSD output1 = cloudTask.declareCSD("output1");
//		CSD output2 = cloudTask.declareCSD("output2");
//		
//		cloudTask.append(x.assign(3));
//		cloudTask.append(y.assign(4));
//		//assign is not supported for CSD?
//		cloudTask.append(output1.assign(sqrt(x*x+y*y)));
//
//		CloudVar i = cloudTask.declareInt("i");
//		CloudVar sum = cloudTask.declareDouble("sum");
//		
//		//for(i=0; i<10; i++) {
//		CloudLoop loop = cloudTask.forLoop(i.assign(0), Lt.apply(i, 10), i.assign(i+1));
//		loop.appendBody(sum.assign(sum+i)); //sum = sum + i
//		//} end for
//		
//		//assign is not supported for CSD?
//		cloudTask.append(output2.assign(sum)); //output = sum
//		
//		cloudTask.compile(null).apply();
//		
////		output1.fetchToLocal();
////		System.out.println(output1.get(0)); //=5
////		
////		output2.fetchToLocal();
////		System.out.println(output2.get(0)); //=55
	}
	
	public static void main(String[] args) {
		//testOverWriteArgs();
		//testReturn();
		//testLoopAsignReturn();
		//MTest1();
		MTest2();
	}
	
	
	public static void MTest1() {
		LCBuilder task = new LCBuilder("local");
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Expr domain = ( Ge.apply(eq, a*a) & Le.apply(eq, b*b) ) | ( Ge.apply(eq, c*c) & Le.apply(eq, d*d) );
		//Expr domain = Ge.apply(eq, a*a) & Le.apply(eq, b*b);
		
		//CloudFunc func = new CloudFunc("MTest1",new LCVar[]{x,y,a,b,c,d}, domain);
		
		task.Return(domain);
		task.build(new LCVar[]{x,y,a,b,c,d});
	}
	
	public static void MTest2() {
		LCBuilder task = new LCBuilder("local");
		
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		LCVar sum = task.declareDouble("sum");
		LCVar counter = task.declareInt("counter");
		LCVar ret = task.declareDouble("ret");
		
		task.append(counter.assign(0)); // All the integers are initialized as 0s
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Expr domain = Ge.apply(1, a) & Ge.apply(1, b);
		
		LCIf ifBranch = task.If(domain);
		
		ifBranch.appendTrue( sum.assign(sum + sin(sqrt(log(x+y+1)))) );// sum = sum + sin(sqrt(log(x+y+1))))
		//ifBranch.appendTrue( sum.assign(sum + x) );
		ifBranch.appendTrue( counter.assign(counter+1) );// counter = counter + 1
		// } //end if
		
		//CloudFunc func = new CloudFunc("MTest1",new LCVar[]{x,y,a,b,c,d}, domain);
		
		task.Return(ret);
		task.build(new LCVar[]{x,y,a,b,c,d});
	}	


}
