package lambdacloud.examples;

import static symjava.math.SymMath.log;
import static symjava.math.SymMath.random;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCIf;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCVar;
import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.relational.Ge;
import symjava.relational.Le;
import symjava.relational.Lt;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;

public class ExampleMonteCarlo {
	
	/**
	 * Monte Carlo integration on two annuluses.
	 * 
	 * Domain: { (x,y) | a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 },
	 * Integrand: sin(sqrt(log(x+y+1))),
	 * where a,b,c,d=0.25,0.5,0.75,1.0
	 * 
	 */
	public static void MonteCarloTwoAnnulusImp1() {
		LCBuilder task = new LCBuilder("server");
		
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
	
	public static void MonteCarloTowAnnulusVerifiy() {
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
	
	
	/**
	 * TODO: Implement bytecodeGen() for class Integrate
	 * Monte Carlo integration on an annulus.
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
	public static void MonteCarloTwoAnnulusImp2() {
		LCBuilder task = new LCBuilder("server");
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Domain omega = new Domain2D("\\Omega", x, y)
			.setConstraint(
					( Ge.apply(eq, a*a) & Le.apply(eq, b*b)) |
					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) ))
			.setBound(x, 0, 1)
			.setBound(y, 0, 1);
		
		Expr I = Integrate.apply(sin(sqrt(log(x+y+1)) ), omega);
		System.out.println(I);
		
		CloudFunc mc = new CloudFunc("MonteCarlo1", new LCVar[]{a, b, c, d}, I);
		CloudSD result = new CloudSD("result");
		CloudSD inputParams = new CloudSD("params");
		inputParams.init(new double[]{0.25, 0.5, 0.75, 1.0});
		mc.apply(result, inputParams);
		
		result.fetchToLocal();
		System.out.println(result.getData(0));
	}
	
	public static void main(String[] args) {
		MonteCarloTwoAnnulusImp1();
		//MonteCarloTwoAnnulusImp2();
		MonteCarloTowAnnulusVerifiy();
	}

}
