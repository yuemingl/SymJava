package lambdacloud.examples;

import static symjava.math.SymMath.log;
import static symjava.math.SymMath.random;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.a;
import static symjava.symbolic.Symbol.b;
import static symjava.symbolic.Symbol.c;
import static symjava.symbolic.Symbol.d;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudIf;
import lambdacloud.core.CloudLocalVar;
import lambdacloud.core.CloudLoop;
import lambdacloud.core.CloudVar;
import lambdacloud.core.LC;
import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.relational.Ge;
import symjava.relational.Le;
import symjava.relational.Lt;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;

public class TestLC {

	public static void main(String[] args) {
		LC cloudTask = new LC("server");
		
		CloudLocalVar x = cloudTask.localVar("x"), y = cloudTask.localVar("y");
		CloudVar output1 = cloudTask.globalVar("output1"), output2 = cloudTask.globalVar("output2");
		
		cloudTask.append(x.assign(3));
		cloudTask.append(y.assign(4));
		cloudTask.append(output1.assign(sqrt(x*x+y*y)));

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
	
	
	/**
	 * Monte Carlo integration on an annular:
	 * 
	 * I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
	 * where 
	 * \Omega= { (x,y), where
	 *             a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or
	 *             c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 
	 *         }
	 * we choose 
	 * a=0.25, b=0.5, c=0.75, d=1.0
	 */
	public static void MonteCarloImplement1() {
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Domain omega = new Domain2D("\\Omega", x, y)
			.setConstraint(
					( Ge.apply(eq, a*a) & Le.apply(eq, b*b)) |
					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) ))
			.setBound(x, 0, 1)
			.setBound(y, 0, 1);
		
		Expr I = Integrate.apply(sin(sqrt(log(x+y+1)) ), omega);
		System.out.println(I);
		
		CloudFunc mc = new CloudFunc("MonteCarlo1", new Expr[]{a, b, c, d}, I);
		CloudVar result = new CloudVar("result");
		CloudVar inputParams = new CloudVar("params");
		inputParams.init(new double[]{0.25, 0.5, 0.75, 1.0});
		mc.apply(result, inputParams);
		
		result.fetchToLocal();
		System.out.println(result.get(0));
	}
	
	public static void MonteCarloImplement2() {
		LC cloudTask = new LC("server");
		
		CloudLocalVar x = cloudTask.localVar("x"); 
		CloudLocalVar y = cloudTask.localVar("y");
		CloudVar result = cloudTask.globalVar("result");
		
		CloudLocalVar i = cloudTask.localVar("i");
		CloudLocalVar sum = cloudTask.localVar("sum");
		CloudLocalVar counter = cloudTask.localVar("counter");
		
		int N = 1000;
		// for(i=0; i<N; i++) {
		CloudLoop loop = cloudTask.forLoop(i.assign(0), Lt.apply(i, N), i.assign(i+1));
		// x = random(); //0.0~1.0
		// y = random(); //0.0~1.0
		loop.appendBody(x.assign(random())); 
		loop.appendBody(y.assign(random()));

		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		// if( a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 ) {
		Expr domain = ( Ge.apply(eq, a*a) & Le.apply(eq, b*b) ) | ( Ge.apply(eq, c*c) & Le.apply(eq, d*d) );
		CloudIf ifBranch = cloudTask.If(domain);
		// sum = sum + sin(sqrt(log(x+y+1))))
		ifBranch.appendTrue( sum.assign(sum + sin(sqrt(log(x+y+1)))) );
		// counter = counter + 1
		ifBranch.appendTrue( counter.assign(counter+1) );
		// } //end fi
		loop.appendBody(ifBranch);
		//} end for

		double squareArea = 1.0;
		Expr area = (counter/N)*squareArea; // area of domain
		cloudTask.append(result.assign((sum/counter)*area)); 
		
		cloudTask.run();
		
		result.fetchToLocal();
		System.out.println(result.get(0));
	}
}
