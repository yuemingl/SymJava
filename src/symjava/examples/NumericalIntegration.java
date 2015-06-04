package symjava.examples;

import symjava.relational.Ge;
import symjava.relational.Le;
import symjava.symbolic.*;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.domains.Domain3D;
import symjava.domains.DomainND;
import symjava.domains.Interval;
import symjava.symbolic.utils.JIT;


public class NumericalIntegration {

	public static void main(String[] args) {
		test_1D();
		test_2D();
		test_ND();
		
		//Expr i = Integrate.apply(exp(pow(x,2)), Interval.apply(a, b).setStepSize(0.001));
		//BytecodeFunc fi = JIT.compile(new Expr[]{a,b}, i);
		//System.out.println(fi.apply(1,2));
		
		test_paper_example1();
		test_paper_example2();
	}
	
	public static void test_1D() {
		//Define the interal
		Domain I = Interval.apply(-10, 0).setStepSize(0.01);
		//Define the integral: cumulative distribution function for standard normal distribution
		Expr cdf = Integrate.apply(exp(-0.5*pow(x,2))/sqrt(2*PI), I);
		System.out.println(cdf); //\int_{-10.0}^{10.0}{1/\sqrt{2*\pi}*e^{-0.5*x^2}}dx
		//Compile cdf to perform numerical integration
		BytecodeFunc f = JIT.compile(cdf);
		System.out.println(f.apply()); //0.5
		
		//Define the integral: cumulative distribution function for a general normal distribution
		Symbol mu = new Symbol("\\mu");
		Symbol sigma = new Symbol("\\sigma");
		Expr cdf2 = Integrate.apply(exp(-pow(x-mu,2)/(2*sigma*sigma))/(sigma*sqrt(2*PI)), I);
		System.out.println(cdf2); //\int_{-10.0}^{0.0}{1/(\sigma*\sqrt{2*\pi})*e^{-(-\mu + x)^2/(2*\sigma*\sigma)}}dx
		//Compile cdf2 to perform numerical integration
		BytecodeFunc f2 = JIT.compile(new Expr[]{mu, sigma}, cdf2);
		System.out.println(f2.apply(-5.0, 1.0)); //~1.0
		
	}
	
	public static void test_2D() {
		//http://turing.une.edu.au/~amth142/Lectures/Lecture_14.pdf
		/*
		Example:
		We will evaluate the integral
			I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
		where \Omega is the disk
			(x-1/2)^2 + (y-1/2)^2 <= 1/4
		Since the disk Ω is contained within the square [0, 1] × [0, 1], we can
		generate x and y as uniform [0, 1] random numbers, and keep those which
		lie in the disk Ω.
		
		Matlab code:
		function ii = monte2da(n)
			k = 0 // count no. of points in disk
			sumf = 0 // keep running sum of function values
			while (k < n) // keep going until we get n points
				x = rand(1,1)
				y = rand(1,1)
				if ((x-0.5)^2 + (y-0.5)^2 <= 0.25) then // (x,y) is in disk
					k = k + 1 // increment count
					sumf = sumf + sin(sqrt(log(x+y+1))) // increment sumf
				end
			end
			ii = (%pi/4)*(sumf/n) // %pi/4 = volume of disk
		endfunction
		-->monte2da(100000)
		ans =
		0.5679196
		*/
		Domain omega = new Domain2D("\\Omega", x, y)
			.setConstraint( Le.apply((x-0.5)*(x-0.5) + (y-0.5)*(y-0.5), 0.25) )
			.setBound(x, 0, 1) 
			.setBound(y, 0, 1);
		
		Expr I = Integrate.apply(sin(sqrt(log(x+y+1))), omega);
		System.out.println(I);
		BytecodeFunc fI = JIT.compile(I);
		System.out.println(fI.apply());
		test_2D_verifiy();
	}
	
	public static void test_2D_verifiy() {
		double xMin=0, xMax=1, xStep=0.001;
		double yMin=0, yMax=1, yStep=0.001;
		double sum = 0.0;
		for(double x=xMin; x<=xMax; x+=xStep) {
			for(double y=yMin; y<=yMax; y+=yStep) {
				if((x-0.5)*(x-0.5) + (y-0.5)*(y-0.5) < 0.5*0.5)
					sum += Math.sin(Math.sqrt(Math.log(x+y+1)))*xStep*yStep;
			}
		}
		System.out.println("verify="+sum);
	}
	
	public static void test_ND() {
		double r = 1.0;
		Domain omega = new Domain3D("\\Omega", x, y, z)
		.setConstraint( Le.apply(x*x + y*y + z*z, r*r) )
		.setBound(x, -1, 1) 
		.setBound(y, -1, 1)
		.setBound(z, -1, 1);
	
		Expr ii = Integrate.apply(1, omega);
		System.out.println(ii);
		BytecodeFunc f = JIT.compile(ii);
		System.out.println(f.apply());
		System.out.println(4.0*Math.PI/3.0*Math.pow(r, 3));

		Domain omega2 = new DomainND("\\Omega", x, y, z, t)
		.setConstraint( Le.apply(x*x + y*y + z*z + t*t, r*r) )
		.setBound(x, -1, 1) 
		.setBound(y, -1, 1)
		.setBound(z, -1, 1)
		.setBound(t, -1, 1);
	
		Expr ii2 = Integrate.apply(1, omega2);
		System.out.println(ii);
		BytecodeFunc f2 = JIT.compile(ii2);
		System.out.println(f2.apply());
		System.out.println(0.5*Math.PI*Math.PI*Math.pow(r, 4));
	
	}
	
	/**
	 * I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
	 * where 
	 * \Omega={ (x,y), where (x-1/2)^2 + (y-1/2)^2 <= 0.25 }
	 */
	public static void test_paper_example1() {
		Domain omega = new Domain2D("\\Omega", x, y)
			.setBound(x, 0.5-sqrt(0.25-(y-0.5)*(y-0.5)), 0.5+sqrt(0.25-(y-0.5)*(y-0.5)))
			.setBound(y, 0, 1)
			.setStepSize(0.001);
		
		Expr I = Integrate.apply( sin(sqrt(log(x+y+1)) ), omega);
		System.out.println(I);
		
		BytecodeFunc fI = JIT.compile(I);
		System.out.println(fI.apply());
	}
	
	/**
	 * I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
	 * where 
	 * \Omega= { (x,y), where
	 *             a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or
	 *             c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 
	 *         }
	 * we choose 
	 * a=0.25, b=0.5, c=0.75, d=1.0
	 */
	public static void test_paper_example2() {
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Domain omega = new Domain2D("\\Omega", x, y)
			.setConstraint(
					( Ge.apply(eq, a*a) & Le.apply(eq, b*b)) |
					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) ))
			.setBound(x, 0, 1)
			.setBound(y, 0, 1);
		
		Expr I = Integrate.apply(sin(sqrt(log(x+y+1)) ), omega);
		System.out.println(I);
		
		BytecodeFunc fI = JIT.compile(new Expr[]{a, b, c, d}, I);
		System.out.println(fI.apply(0.25, 0.5, 0.75, 1.0));
		
		test_paper_example_verifiy();
	}
	
	public static void test_paper_example_verifiy() {
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
