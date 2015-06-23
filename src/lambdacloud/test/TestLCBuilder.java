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
		MonteCarloImplement2();
		//MTest1();
		//MTest2();
		MonteCarloImplementVerifiy();
	}
	
	
//	/**
//	 * Monte Carlo integration on an annular:
//	 * 
//	 * I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
//	 * where 
//	 * \Omega= { (x,y), where
//	 *             a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or
//	 *             c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 
//	 *         }
//	 * we choose 
//	 * a=0.25, b=0.5, c=0.75, d=1.0
//	 */
//	public static void MonteCarloImplement1() {
//		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
//		Domain omega = new Domain2D("\\Omega", x, y)
//			.setConstraint(
//					( Ge.apply(eq, a*a) & Le.apply(eq, b*b)) |
//					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) ))
//			.setBound(x, 0, 1)
//			.setBound(y, 0, 1);
//		
//		Expr I = Integrate.apply(sin(sqrt(log(x+y+1)) ), omega);
//		System.out.println(I);
//		
//		CloudFunc mc = new CloudFunc("MonteCarlo1", new Expr[]{a, b, c, d}, I);
//		CSD result = new CSD("result");
//		CSD inputParams = new CSD("params");
//		inputParams.init(new double[]{0.25, 0.5, 0.75, 1.0});
//		mc.apply(result, inputParams);
//		
//		result.fetchToLocal();
//		System.out.println(result.get(0));
//	}
//	
	
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
		CloudFunc func = task.build(new LCVar[]{x,y,a,b,c,d});
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
		
		task.append(counter.assign(0));
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Expr domain = Ge.apply(1, a) & Ge.apply(1, b);
		LCIf ifBranch = task.If(domain);
		// sum = sum + sin(sqrt(log(x+y+1))))
		ifBranch.appendTrue( sum.assign(sum + sin(sqrt(log(x+y+1)))) );
		//ifBranch.appendTrue( sum.assign(sum + x) );
		// counter = counter + 1
		ifBranch.appendTrue( counter.assign(counter+1) );
		// } //end if
		//CloudFunc func = new CloudFunc("MTest1",new LCVar[]{x,y,a,b,c,d}, domain);
		task.Return(ret);
		CloudFunc func = task.build(new LCVar[]{x,y,a,b,c,d});
	}	

	
	
	public static void MonteCarloImplement2() {
		LCBuilder task = new LCBuilder("local");
		
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		LCInt i = task.declareInt("i");
		LCVar sum = task.declareDouble("sum");
		LCVar counter = task.declareInt("counter");
		
		int N = 10000000;
		
		LCLoop loop = task.For(i.assign(0), 
				Lt.apply(i, N), i.inc());    // for(i=0; i<N; i++) {
		
		loop.appendBody(x.assign(random())); // x = random(); //0.0~1.0
		loop.appendBody(y.assign(random())); // y = random(); //0.0~1.0
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Expr domain = (                      // if( a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 ) {
				Ge.apply(eq, a*a) & Le.apply(eq, b*b) ) | 
				( Ge.apply(eq, c*c) & Le.apply(eq, d*d) 
				);
		LCIf ifBranch = new LCIf(domain);
		ifBranch.appendTrue(sum.assign(sum + sin(sqrt(log(x+y+1))))); // sum = sum + sin(sqrt(log(x+y+1))))
		ifBranch.appendTrue(counter.assign(counter+1));               // counter = counter + 1
		// } //end if
		loop.appendBody(ifBranch);
		//} end for

		double squareArea = 1.0;
		Expr area = (counter/N)*squareArea; // area of domain
		task.Return((sum/counter)*area); 
		
		CloudFunc func = task.build(new LCVar[]{a,b,c,d});
		
		CloudSD params = new CloudSD("result").init(new double[]{0.25,0.5,0.75,1.0});
		CloudSD result = new CloudSD("result").resize(1);
		func.apply(result, params);
		
		result.fetchToLocal();
		System.out.println(result.getData(0));
	}
	
	public static void MonteCarloImplementVerifiy() {
		double xMin=0, xMax=1, xStep=0.001;
		double yMin=0, yMax=1, yStep=0.001;
		double sum = 0.0;
		double a=0.25, b=0.5, c=0.75, d=1.0;
		for(double x=xMin; x<=xMax; x+=xStep) {
			for(double y=yMin; y<=yMax; y+=yStep) {
				double disk = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
				if((a*a <= disk && disk <= b*b) || 
				   (c*c <= disk && disk <= d*d )
				  ) {
					sum += Math.sin(Math.sqrt(Math.log(x+y+1)))*xStep*yStep;
				}
			}
		}
		System.out.println("verify="+sum);
	}	
}
