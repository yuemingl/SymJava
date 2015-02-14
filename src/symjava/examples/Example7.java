package symjava.examples;

import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import symjava.domains.Domain;
import symjava.examples.fem.Mesh2D;
import symjava.examples.fem.Mesh2DBoundary;
import symjava.examples.fem.Node;
import symjava.examples.fem.RefLine;
import symjava.examples.fem.RefTriangle;
import symjava.math.Transformation;
import symjava.matrix.SymMatrix;
import symjava.numeric.NumFunc;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Int;
import symjava.symbolic.SymConst;
import symjava.symbolic.utils.ExprPair;
import symjava.symbolic.utils.Utils;

/**
 * Finite Element solver
 * 
 */
public class Example7 {
	public static void main(String[] args) {
		Mesh2D mesh = new Mesh2D("S33");
		mesh.readGridGenMesh("triangle.grd");

		Func u = new Func("u", x, y);
		Func v = new Func("v", x, y);

		final double eps = 1e-5;
		Domain neumannBC = mesh.getBoundary(new NumFunc<Integer>() {
			{
				label = "neumannBC";
			}
			@Override
			public Integer apply(double ...args) {
				double x = args[0], y = args[1];
				if(Math.abs(3-Math.abs(x))<eps)
					return 2; //
				return 0;
			}
		});
		Eq pde = new Eq(
					Int.apply(a*dot(grad(u), grad(v)), mesh) + Int.apply((d*u-g)*v, neumannBC),
					(-2*(x*x+y*y)+36)*v
				);
		
		//Mark boundary nodes
		for(Node n : mesh.nodes) {
			if(Math.abs(3-Math.abs(n.coords[1]))<eps)
				n.setType(1);
		}
		Map<Integer, Double> diri = new HashMap<Integer, Double>();
		diri.put(1, 0.0);
		
		//So
		solve(pde, mesh, diri, "triangle.dat");
		
	}
	
	public static void solve(Eq pde, Mesh2D mesh, Map<Integer, Double> dirichlet, String output) {
		System.out.println(String.format("PDE Weak Form: %s = %s", pde.lhs, pde.rhs));
		
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
		Expr ry = -jacMat[0][1]/jac; //ry = -xs/jac //bugfix missed a minus sign!
		Expr sx = -jacMat[1][0]/jac; //sx = -yr/jac
		Expr sy =  jacMat[0][0]/jac; //sy =  xr/jac
		System.out.println(jac);
		System.out.println(rx);
		System.out.println(ry);
		System.out.println(sx);
		System.out.println(sy);

		RefTriangle refTri = new RefTriangle("RefT", r, s);
		RefLine refLine = new RefLine("RefL", r);
		
		Transformation transB = new Transformation(
				new Eq(x, x1*r+x2*(1-r)),
				new Eq(y, y1*r+y2*(1-r))
				);
		transB.getJacobian();
		Func NB1 = new Func("RB");
		Func NB2 = new Func("SB");
		Func[] shapeBFuns = {NB1, NB2};
		Expr jacB = sqrt(pow(x1-x2,2)+pow(y1-y2,2)); //transB.getJacobian();
		
//		List<Expr> lhsInt[][] = new List<Expr>[shapeFuns.length][shapeFuns.length];
//		List<Expr> rhsInt[] = new List<Expr>[shapeFuns.length];
		
		for(int i=0; i<shapeFuns.length; i++) {
			Func V = shapeFuns[i]; //test
			for(int j=0; j<shapeFuns.length; j++) {
				Func U = shapeFuns[j]; //trial
				
				//Substitute u, v to shape functions
				Expr lhs = pde.lhs.subs(u, U).subs(v, V);
				System.out.println(lhs);
				System.out.println();
				
				//Create substitution list for change of variables
				List<ExprPair> subsList = new ArrayList<ExprPair>();
				subsList.add(new ExprPair(N1.diff(x), rx));
				subsList.add(new ExprPair(N1.diff(y), ry));
				subsList.add(new ExprPair(N2.diff(x), sx));
				subsList.add(new ExprPair(N1, r));
				subsList.add(new ExprPair(N2, s));
				subsList.add(new ExprPair(x, trans.eqs[0].rhs));
				subsList.add(new ExprPair(y, trans.eqs[1].rhs));
				
				List<ExprPair> subsListB = new ArrayList<ExprPair>();
				subsListB.add(new ExprPair(NB1, r));
				subsListB.add(new ExprPair(NB2, s));
				subsListB.add(new ExprPair(x, trans.eqs[0].rhs));
				subsListB.add(new ExprPair(y, trans.eqs[1].rhs));


				List<Expr> addList = new ArrayList<Expr>();
				lhs.flattenAdd(addList);
				List<Expr> lhsList = new ArrayList<Expr>();
				for(Expr term : addList) {
					Int intTerm = (Int)term;
					if(intTerm.domain instanceof Mesh2D)
						lhsList.add(intTerm.changeOfVars(subsList, jac, refTri));
					else if(intTerm.domain instanceof Mesh2DBoundary)
						lhsList.add(intTerm.changeOfVars(subsListB, jacB, refLine));
				}
				
				System.out.println(Utils.addListToExpr(lhsList));
				
//					
//				//lhsInt[i][j] = new Int(new Func("",lhs*jac,new Expr[]{r,s}), tri);
//				lhsInt[i][j] = new Int(new Func(
//						String.format("lhs%d%d",i,j), lhs*jac,new Expr[]{r,s}), tri);
//				System.out.println(lhsInt[i][j]);
//				System.out.println();
//				}
			}
//			//Weak form for the right hand side of the PDE
//			Expr rhs = pde.rhs.subs(v, V)
//					.subs(N1, r).subs(N2, s)
//					.subs(x, trans.eqs[0].rhs)
//					.subs(y, trans.eqs[1].rhs);
//			System.out.println(rhs);
//			System.out.println();
////			rhsInt[i] = new Int(new Func(
////					String.format("rhs%d",i),rhs*jac,new Expr[]{r,s}), tri);
		}
		
//		//Generate bytecode for the integration
//		//You can save the class to some place for later use
//		NumInt lhsNInt[][] = new NumInt[shapeFuns.length][shapeFuns.length];
//		NumInt rhsNInt[] = new NumInt[shapeFuns.length];
//		for(int i=0; i<shapeFuns.length; i++) {
//			for(int j=0; j<shapeFuns.length; j++) {
//				lhsNInt[i][j] = new NumInt(lhsInt[i][j]);
//			}
//			rhsNInt[i] = new NumInt(rhsInt[i]);
//		}
//
//		//Assemble the system
//		System.out.println("Start assemble the system...");
//		long begin = System.currentTimeMillis();
//		List<Domain> eles = mesh.getSubDomains();
//		double[][] matA = new double[mesh.nodes.size()][mesh.nodes.size()];
//		double[] vecb = new double[mesh.nodes.size()];
//		for(Domain d : eles) {
//			Element e = (Element)d;
//			double[] nodeCoords = e.getNodeCoords();
//			for(int i=0; i<shapeFuns.length; i++) {
//				int idxI =  e.nodes.get(i).getIndex()-1;
//				for(int j=0; j<shapeFuns.length; j++) {
//					int idxJ = e.nodes.get(j).getIndex()-1;
//					double t = lhsNInt[i][j].eval(nodeCoords);
//					//System.out.println(idxI+" "+idxJ+" "+t);
//					matA[idxI][idxJ] += t;
//				}
//				vecb[idxI] += rhsNInt[i].eval(nodeCoords);
//			}
//		}
//		System.out.println("Assemble done! Time: "+(System.currentTimeMillis()-begin)+"ms");
//		
//		System.out.println("Solving...");
//		begin = System.currentTimeMillis();
//		Matrix A = new Matrix(matA);
//		Matrix b = new Matrix(vecb, vecb.length);
//		if(dirichlet != null) {
//			for(Node n : mesh.nodes) {
//				Double diri = dirichlet.get(n.getType());
//				if(diri != null) {
//					setDirichlet(A, b, n.getIndex()-1, diri);
//				}
//			}
//		}
//		Matrix x = A.solve(b);
////		for(int i=0; i<x.getRowDimension(); i++)
////			System.out.println(x.get(i, 0));
//		mesh.writeTechplot(output, x.getArray());
//		System.out.println("Solved! Time: "+(System.currentTimeMillis()-begin)+"ms");
//		System.out.println("See the output file(Tecplot format) "+output+" for the solution.");
	}
	
	public static void setDirichlet(Matrix A, Matrix b, int nodeIndex, double value) {
		int row = nodeIndex;
		int col = nodeIndex;
		A.set(row, col, 1.0);
		b.set(row, 0, value);
		for(int r=0; r<A.getRowDimension(); r++) {
			if(r != row) {
				A.set(r, col, 0.0);
				b.set(r, 0 , b.get(r, 0)-A.get(r, col)*value);
			}
		}
		for(int c=0; c<A.getColumnDimension(); c++) {
			if(c != col) {
				A.set(row, c, 0.0);
			}
		}
	}	
}
