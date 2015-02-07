package symjava.examples;

import static symjava.math.SymMath.dot;
import static symjava.math.SymMath.grad;
import static symjava.symbolic.Symbol.*;

import java.util.List;

import Jama.Matrix;
import symjava.math.Transformation;
import symjava.matrix.SymMatrix;
import symjava.numeric.NumInt;
import symjava.relational.Eq;
import symjava.symbolic.Domain;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Int;
import symjava.symbolic.SymConst;

public class Example6 {
	public static void main(String[] args) {
		//Read the mesh
		Mesh2D mesh = new Mesh2D("mesh1", x,y);
		mesh.readTriangleMesh("double_hex3.1.node", "double_hex3.1.ele");
		
		//Our PDE equation
		Func u = new Func("u", x, y);
		Func v = new Func("v", x, y);
		Eq pde = new Eq(0.5*dot(grad(u), grad(v)) + 0.1*u*v, (x*x+y*y)*v);
		
		solve(pde, mesh);
	}
	
	public static void solve(Eq pde, Mesh2D mesh) {
		UnitRightTriangle tri = new UnitRightTriangle("Tri", r, s);
		
		//Create coordinate transformation
		SymConst x1 = new SymConst("x1");
		SymConst x2 = new SymConst("x2");
		SymConst x3 = new SymConst("x3");
		SymConst y1 = new SymConst("y1");
		SymConst y2 = new SymConst("y2");
		SymConst y3 = new SymConst("y3");
		Transformation trans = new Transformation(
				new Eq(x, x1*r+x2*s+x3*(1-r-s)),
				new Eq(y, y1*r+y2*s+y3*(1-r-s))
				);
		// jac = (xr xs)
		//       (yr ys)
		SymMatrix jacMat = trans.getJacobianMatrix();
		System.out.println(jacMat);
		System.out.println();
		
		//Shape functions
		Func N1 = new Func("R", x, y);
		Func N2 = new Func("S", x, y);
		Func N3 = new Func("T", 1 - N1 - N2);
		Func[] shapeFuns = {N1, N2, N3};
		
		Expr jac = trans.getJacobian();
		Expr rx =  jacMat[1][1]/jac; //rx =  ys/jac
		Expr ry =  jacMat[0][1]/jac; //ry =  xs/jac
		Expr sx = -jacMat[1][0]/jac; //sx = -yr/jac
		Expr sy =  jacMat[0][0]/jac; //sy =  xr/jac

		Int lhsInt[][] = new Int[shapeFuns.length][shapeFuns.length];
		Int rhsInt[] = new Int[shapeFuns.length];
		for(int i=0; i<shapeFuns.length; i++) {
			Func U = shapeFuns[i];
			for(int j=0; j<shapeFuns.length; j++) {
				Func V = shapeFuns[j];
				
				//Weak form for the left hand side of the PDE
				Expr lhs = pde.lhs.subs(u, U).subs(v, V);
				System.out.println(lhs);
				System.out.println();
				
				//Replace the derivatives with it's concrete expression
				lhs = lhs
					.subs(N1.diff(x), rx).subs(N1.diff(y), ry)
					.subs(N2.diff(x), sx).subs(N2.diff(y), sy)
					.subs(N1, r).subs(N2, s)
					.subs(x, trans.eqs[0].rhs)
					.subs(y, trans.eqs[1].rhs);
				
				//Define the integration on the reference domain
				lhsInt[i][j] = new Int(new Func("",lhs*jac,new Expr[]{r,s}), tri);
				//System.out.println(I[i][j]);
			}
			//Weak form for the right hand side of the PDE
			Expr rhs = pde.rhs.subs(v, U).
					subs(N1, r).subs(N2, s)
					.subs(x, trans.eqs[0].rhs)
					.subs(y, trans.eqs[1].rhs);
			System.out.println(rhs);
			System.out.println();
			rhsInt[i] = new Int(new Func("",rhs*jac,new Expr[]{r,s}), tri);
		}
		
		//Generate bytecode for the integration
		//You can save the class to some place for later use
		NumInt lhsNInt[][] = new NumInt[shapeFuns.length][shapeFuns.length];
		NumInt rhsNInt[] = new NumInt[shapeFuns.length];
		for(int i=0; i<shapeFuns.length; i++) {
			for(int j=0; j<shapeFuns.length; j++) {
				lhsNInt[i][j] = new NumInt(lhsInt[i][j]);
			}
			rhsNInt[i] = new NumInt(rhsInt[i]);
		}

		//Assemble the system
		List<Domain> eles = mesh.getSubDomains();
		double[][] matA = new double[mesh.nodes.size()][mesh.nodes.size()];
		double[][] vecb = new double[1][mesh.nodes.size()];
		for(Domain d : eles) {
			Element e = (Element)d;
			double[] nodeCoords = e.getNodeCoords();
			for(int i=0; i<shapeFuns.length; i++) {
				int idxI =  e.nodes.get(i).index-1;
				for(int j=0; j<shapeFuns.length; j++) {
					int idxJ = e.nodes.get(j).index-1;
					matA[idxI][idxJ] += lhsNInt[i][j].eval(nodeCoords);
				}
				vecb[0][idxI] = rhsNInt[i].eval(nodeCoords);
			}
		}
		
		Matrix A = new Matrix(matA);
		Matrix b = new Matrix(vecb);
		Matrix x = A.solve(b);
		for(int i=0; i<x.getRowDimension(); i++) {
			System.out.println(x.get(i, 0));
		}
	}
}
