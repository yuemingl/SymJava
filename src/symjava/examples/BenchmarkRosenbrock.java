package symjava.examples;

import static symjava.math.SymMath.pow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import symjava.math.SymMath;
import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.numeric.NumMatrix;
import symjava.numeric.NumVector;
import symjava.symbolic.Expr;
import symjava.symbolic.Sum;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.Utils;

public class BenchmarkRosenbrock {

	public static double test(int N) {
		long begin, end;
		Expr rosen = null;
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		Symbols xim1 = new Symbols("x", i-1);
		rosen  =  Sum.apply(100*pow(xi-xim1*xim1,2) + pow(1-xim1,2), i, 2, N);
		//System.out.println("Rosenbrock function with N="+N+": "+rosen);

		boolean debug = false;
		PrintWriter pw = null;
		String genFileName = "benchmark-rosenbrock"+N+"-manual.cpp";
		try {
			pw = new PrintWriter(genFileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		print_header(pw);
		
		Expr[] freeVars = xi.get(1, N);
		begin = System.currentTimeMillis();
		SymVector grad = SymMath.grad(rosen);
		SymMatrix hess = SymMath.hess(rosen);
		end = System.currentTimeMillis();
		double timeSym = (end-begin)/1000.0;
		
		begin = System.currentTimeMillis();
		NumVector numGrad = grad.toNumVector(freeVars);
		end = System.currentTimeMillis();
		double timeGrad = (end-begin)/1000.0;


		begin = System.currentTimeMillis();
		NumMatrix numHess = hess.toNumMatrix(freeVars);
		end = System.currentTimeMillis();
		double timeHess = (end-begin)/1000.0;

		double[] outAry = null;
		
		double[] args = new double[freeVars.length];
		outAry = new double[N];
		for(int k=0; k<N; k++)
			args[k] = 1.0;
		for(int k=0; k<N; k++)
			args[k] += 1e-15;
		double[] gradResult = numGrad.eval(outAry, args);
		if(debug) {
			System.out.println(grad);
			for(double d : gradResult)
				System.out.println(d);
			System.out.println();
		}
		print_c_code(pw, grad);

		
		outAry = new double[N*N];
		numHess.eval(outAry, args);
		double[][] hessResult = numHess.copyData();
		if(debug) {
			System.out.println(hess);
			for(double[] row : hessResult) {
				for(double d : row)
					System.out.print(d+" ");
				System.out.println();
			}
		}

		print_c_code(pw, hess);
		print_main(pw, N);
		pw.close();
		Runtime r = Runtime.getRuntime();
		Process p;
		double timeCCompile = 0.0;
		try {
			begin = System.currentTimeMillis();
			String execStr = "g++ -O3 ./"+genFileName+" -o run"+N;
			//String execStr = "g++ ./"+genFileName+" -o run"+N;
			//System.out.println(execStr);
			p = r.exec(execStr);
			p.waitFor();
			end = System.currentTimeMillis();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = b.readLine()) != null) {
			  System.out.println(line);
			}
			b.close();
			timeCCompile = (end-begin)/1000.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//===================Benchmark==============
		
		int NN = 100000;
		outAry = new double[N];
		begin = System.currentTimeMillis();
		double out = 0.0;
		for(int j=0; j<NN; j++) {
			for(int k=0; k<N; k++)
				args[k] += 1e-15;
			numGrad.eval(outAry, args);
			for(int k=0; k<N; k++)
				out += outAry[k];
		}
		end = System.currentTimeMillis();
		double timeGradEval = (end-begin)/1000.0;
		
		outAry = new double[N*N];
		begin = System.currentTimeMillis();
		for(int j=0; j<NN; j++) {
			for(int k=0; k<N; k++)
				args[k] += 1e-15;
			numHess.eval(outAry, args);
			for(int k=0; k<N; k++) //Trace
				out += outAry[k*N+k];
		}
		end = System.currentTimeMillis();
		double timeHessEval = (end-begin)/1000.0;

		System.out.println(N+"\t"+timeSym+"\t"+timeGrad+"\t"+timeGradEval+"\t"+timeHess+"\t"+timeHessEval+"\t"+timeCCompile);

		return out;
	}
	
	public static void print_header(PrintWriter writer) {
		writer.println("#include <iostream>");
		writer.println("#include <ctime>");
		writer.println("#include <cstring>");
		writer.println("using namespace std;");
		writer.println();

		writer.println("  inline double pow(double x, int exp) {");
		writer.println("    if(exp == 0) return 1.0;");
		writer.println("    else if(exp == 1) return x;");
		writer.println("    else if(exp < 0) return 1.0/pow(x, -exp);");
		writer.println("    else {");
		writer.println("      int tmpExp = exp >> 1;");
		writer.println("      double xx = x;");
		writer.println("      double rlt = exp & 0x1 ? x : 1.0;");
		writer.println("      do {");
		writer.println("        xx *= xx;");
		writer.println("        if(tmpExp & 0x1) {");
		writer.println("          rlt *= xx;");
		writer.println("        }");
		writer.println("      } while (tmpExp >>= 1);");
		writer.println("      return rlt;");
		writer.println("    }");
		writer.println("  }");
		writer.println();
	}
	
	public static void print_main(PrintWriter writer, int N) {
		writer.println();
		writer.println("int main() {");
		writer.println("	clock_t start;");
		writer.println("	double durationGrad, durationHess;");
		writer.println("	int N = 100000;");
		writer.println("	double *args, *outAry;");
		writer.println("	double out = 0.0;");
		writer.println();
			writer.println("	args = new double["+N+"];");
			writer.println("	for(int i=0; i<"+N+"; i++)");
			writer.println("		args[i] = 1.0;");
			writer.println("	outAry = new double["+(N*N)+"];");
			writer.println("	start = std::clock();");
			writer.println("	for(int i=0; i<N; i++) {");
			writer.println("		for(int j=0; j<"+N+"; j++) {");
			writer.println("			args[j] += 1e-15;");
			writer.println("		}");
			writer.println("		grad_"+N+"(args, outAry);");
			writer.println("		for(int j=0; j<"+N+"; j++) {");
			writer.println("			out += outAry[j];");
			writer.println("		}");
//			writer.println("		out += outAry["+(N-1)+"];");
			writer.println("	}");
			writer.println("	durationGrad = ( std::clock() - start ) / (double) CLOCKS_PER_SEC;");
			writer.println("	start = std::clock();");
			writer.println("	for(int i=0; i<N; i++) {");
			writer.println("		for(int j=0; j<"+N+"; j++) {");
			writer.println("			args[j] += 1e-15;");
			writer.println("		}");
			writer.println("		hess_"+N+"(args, outAry);");
//			writer.println("		for(int j=0; j<"+N+"*"+N+"; j++) {");
			writer.println("		for(int j=0; j<"+N+"; j++) {");
			writer.println("			out += outAry[j*"+N+"+j];");
			writer.println("		}");
//			writer.println("		out += outAry["+(N*N-1)+"];");
			writer.println("	}");
			writer.println("	durationHess = ( std::clock() - start ) / (double) CLOCKS_PER_SEC;");
			writer.println("	cout<<\"N="+N+": Grad=\"<< durationGrad << \" Hess=\" << durationHess << endl;");
			writer.println("	delete args;");
			writer.println("	delete outAry;");
			writer.println();
		writer.println("	cout<<\" Final Value=\" << out << endl;");
		writer.println("}");
		writer.println("//g++ -O3 benchmark-rosenbrock-manual.cpp -o run");
	}

	public static void print_c_code(PrintWriter pw, SymVector grad) {
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		pw.println("void grad_"+grad.dim()+"(double* args, double* outAry) {");
		for(int j=0; j<grad.dim(); j++) {
			Expr row = grad.get(j);
			Symbol xj1 = new Symbol("args["+(j-1)+"]");
			Symbol xj2 = new Symbol("args["+(j)+"]");
			Symbol xj3 = new Symbol("args["+(j+1)+"]");
			row = row.subs(xi.get(j), xj1)
					.subs(xi.get(j+1), xj2)
					.subs(xi.get(j+2), xj3)
					;
			pw.println("\toutAry["+j+"]="+row+";");
		}
		pw.println("}");
	}
	
	public static void print_c_code(PrintWriter pw, SymMatrix hess) {
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		pw.println("void hess_"+hess.rowDim()+"(double* args, double* outAry) {");
		int idx = 0;
		for(int j=0; j<hess.rowDim(); j++) {
			for(int k=0; k<hess.colDim(); k++) {
				Expr row = hess[j][k];
				if(!Utils.symCompare(row, Symbol.C0)) {
					Symbol xj1 = new Symbol("args["+(j-1)+"]");
					Symbol xj2 = new Symbol("args["+(j)+"]");
					Symbol xj3 = new Symbol("args["+(j+1)+"]");
					row = row.subs(xi.get(j), xj1)
							.subs(xi.get(j+1), xj2)
							.subs(xi.get(j+2), xj3)
							;
					pw.println("\toutAry["+idx+"] = "+row+";");
				}
				idx++;
			}
		}
		pw.println("}");
	}
	
	public static void main(String[] args) {
		System.out.println("============Benchmark for Rosenbrock==============");
		System.out.println("N|Symbolic Manipulaton|Compile Gradient|Eval Gradient|Compile Hessian|Eval Hessian|C Code Compile");
		double out = 0.0;
		for(int N=5; N<850; N+=50) {
			double tmp = test(N);
			System.out.println("Final Value="+tmp);
			out += tmp;
		}
		System.out.println("Final Value="+out);//6.881736000015767E11
	}
}
