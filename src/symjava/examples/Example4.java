package symjava.examples;

import java.util.ArrayList;
import java.util.List;

import symjava.matrix.*;
import symjava.symbolic.*;
import symjava.symbolic.utils.Utils;
import static symjava.symbolic.Symbol.*;

/**
 * Example for PDE Constrained Parameters Optimization
 *
 */
public class Example4 {
	public static SymVector grad(Func f) {
		SymVector g = new SymVector();
		for(Expr arg : f.args) {
			g.add(f.diff(arg));
		}
		return g;
	}
	public static Expr dot(SymVector a, SymVector b) {
		if(a.dim() != b.dim()) 
			return null;
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<a.dim(); i++) {
			list.add(a.get(i).multiply(b.get(i)));
		}
		return Utils.addListToExpr(list);
	}
	
	//Functional derivative
	public static Expr fdiff(Expr F, Expr f, Expr df) {
		Symbol alpha = new Symbol("_alpha_");
		Expr ff = F.subs(f, f+alpha*df);
		Expr dff = ff.diff(alpha);
		return dff.subs(alpha, 0).simplify();
	}
	
	//Functional grad
	public static SymVector fgrad(Expr F, Expr[] fs, Expr[] dfs) {
		SymVector g = new SymVector();
		for(int i=0; i<fs.length; i++) {
			g.add(fdiff(F, fs[i], dfs[i]));
		}
		return g;
	}
	
	public static void main(String[] args) {
		Func u =  new Func("u",  x,y,z);
		Func u0 = new Func("u0", x,y,z);
		Func q =  new Func("q",  x,y,z);
		Func q0 = new Func("q0", x,y,z);
		Func f =  new Func("f",  x,y,z);
		Func lamd = new Func("\\lambda ", x,y,z);
		
		Expr reg_term = (q-q0)*(q-q0)*0.5*0.1;

		Expr L = (u-u0)*(u-u0)/2 + reg_term + q*dot(grad(u), grad(lamd)) - f*lamd;
		System.out.println("Lagrange L(u, \\lambda, q) = \n"+L);
		
		Func phi = new Func("\\phi ", x,y,z);
		Func psi = new Func("\\psi ", x,y,z);
		Func chi = new Func("\\chi ", x,y,z);
		Expr[] xs =  new Expr[]{u,   lamd, q   };
		Expr[] dxs = new Expr[]{phi, psi,  chi };
		SymVector Lx = fgrad(L, xs, dxs);
		System.out.println("\nGradient Lx = (Lu, Llamd, Lq) =");
		Lx.print();
		
		Func du = new Func("\\delta{u}", x,y,z);
		Func dl = new Func("\\delta{\\lambda}", x,y,z);
		Func dq = new Func("\\delta{q}", x,y,z);
		Expr[] dxs2 = new Expr[] { du, dl, dq };
		SymMatrix Lxx = new SymMatrix();
		for(Expr Lxi : Lx) {
			Lxx.add(fgrad(Lxi, xs, dxs2));
		}
		System.out.println("\nHessian Matrix =");
		Lxx.print();
	}
}
