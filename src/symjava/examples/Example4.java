package symjava.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import static symjava.symbolic.Symbol.z;
import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.symbolic.Dot;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Grad;

/**
 * Example for PDE Constrained Parameters Optimization
 *
 */
public class Example4 {
	public static void main(String[] args) {
		Func u =  new Func("u",  x,y,z);
		Func u0 = new Func("u0", x,y,z);
		Func q =  new Func("q",  x,y,z);
		Func q0 = new Func("q0", x,y,z);
		Func f =  new Func("f",  x,y,z);
		Func lamd = new Func("\\lambda ", x,y,z);
		
		Expr reg_term = (q-q0)*(q-q0)*0.5*0.1;

		//Func L = new Func("L",(u-u0)*(u-u0)/2 + reg_term + q*Dot.apply(Grad.apply(u), Grad.apply(lamd)) - f*lamd);
		Func L = new Func("L",Dot.apply(Grad.apply(u), Grad.apply(lamd)));
		
		System.out.println("Lagrange L(u, \\lambda, q) = \n"+L);
		
		Func phi = new Func("\\phi ", x,y,z);
		Func psi = new Func("\\psi ", x,y,z);
		Func chi = new Func("\\chi ", x,y,z);
		Expr[] xs =  new Expr[]{u,   lamd, q   };
		Expr[] dxs = new Expr[]{phi, psi,  chi };
		SymVector Lx = Grad.apply(L, xs, dxs);
		System.out.println("\nGradient Lx = (Lu, Llamd, Lq) =");
		Lx.print();
		
		Func du = new Func("\\delta{u}", x,y,z);
		Func dl = new Func("\\delta{\\lambda}", x,y,z);
		Func dq = new Func("\\delta{q}", x,y,z);
		Expr[] dxs2 = new Expr[] { du, dl, dq };
		SymMatrix Lxx = new SymMatrix();
		for(Expr Lxi : Lx) {
			Lxx.add(Grad.apply(Lxi, xs, dxs2));
		}
		System.out.println("\nHessian Matrix =");
		Lxx.print();
	}
}
