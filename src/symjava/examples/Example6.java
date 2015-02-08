package symjava.examples;

import static symjava.math.SymMath.dot;
import static symjava.math.SymMath.grad;
import static symjava.symbolic.Symbol.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import symjava.bytecode.BytecodeFunc;
import symjava.math.Transformation;
import symjava.matrix.SymMatrix;
import symjava.numeric.NumInt;
import symjava.relational.Eq;
import symjava.symbolic.Domain;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Int;
import symjava.symbolic.SymConst;

/**
 * Finite Element solver
 * 
 */
public class Example6 {
	public static void main(String[] args) {
		Func u = new Func("u", x, y);
		Func v = new Func("v", x, y);

//		//Our PDE equation
//		Eq pde = new Eq(0.5*dot(grad(u), grad(v)) + 0.1*u*v, (x*x+y*y)*v);
//		//Read the mesh
//		Mesh2D mesh = new Mesh2D("mesh1", x, y);
//		mesh.readTriangleMesh("double_hex3.1.node", "double_hex3.1.ele");
//		solve(pde, mesh, null, "double_hex3.1.dat");

		//Another PDE equation with Dirichlet condition
		Eq pde2 = new Eq(dot(grad(u), grad(v)), (-2*(x*x+y*y)+36)*v);
		//Eq pde2 = new Eq(u*v, (-2*(x*x+y*y)+36)*v);
		Mesh2D mesh2 = new Mesh2D("mesh2", x, y);
		//mesh2.readGridGenMesh("patch_triangle.grd");
		mesh2.readGridGenMesh("triangle.grd");
		//Mark boundary nodes
		double eps = 0.01;
		for(Node n : mesh2.nodes) {
			//if(1-Math.abs(n.coords[0])<eps || 1-Math.abs(n.coords[1])<eps || Math.abs(n.coords[0])<eps || Math.abs(n.coords[1])<eps )
			if(Math.abs(3-Math.abs(n.coords[0]))<eps || Math.abs(3-Math.abs(n.coords[1]))<eps)
				n.setType(1);
		}
		Map<Integer, Double> diri = new HashMap<Integer, Double>();
		diri.put(1, 0.0);
		//solve(pde2, mesh2, diri, "patch_triangle.dat");
		solve(pde2, mesh2, diri, "triangle.dat");
		
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
//		Expr rx =  jacMat[1][1]; //rx =  ys/jac
//		Expr ry =  jacMat[0][1]; //ry =  xs/jac
//		Expr sx = -jacMat[1][0]; //sx = -yr/jac
//		Expr sy =  jacMat[0][0]; //sy =  xr/jac
		System.out.println(jac);
		System.out.println(rx);
		System.out.println(ry);
		System.out.println(sx);
		System.out.println(sy);

		UnitRightTriangle tri = new UnitRightTriangle("Tri", r, s);
		Int lhsInt[][] = new Int[shapeFuns.length][shapeFuns.length];
		Int rhsInt[] = new Int[shapeFuns.length];
		for(int i=0; i<shapeFuns.length; i++) {
			Func V = shapeFuns[i]; //test
			for(int j=0; j<shapeFuns.length; j++) {
				Func U = shapeFuns[j]; //trial
				
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
				System.out.println(lhs);
				System.out.println();
				
				//Define the integration on the reference domain
				//lhsInt[i][j] = new Int(new Func("",lhs*jac,new Expr[]{r,s}), tri);
				lhsInt[i][j] = new Int(new Func(
						String.format("lhs%d%d",i,j), lhs*jac,new Expr[]{r,s}), tri);
				System.out.println(lhsInt[i][j]);
				System.out.println();
			}
			//Weak form for the right hand side of the PDE
			Expr rhs = pde.rhs.subs(v, V)
					.subs(N1, r).subs(N2, s)
					.subs(x, trans.eqs[0].rhs)
					.subs(y, trans.eqs[1].rhs);
			//System.out.println(rhs);
			System.out.println();
			rhsInt[i] = new Int(new Func(
					String.format("rhs%d",i),rhs*jac,new Expr[]{r,s}), tri);
		}
		
//		Expr ttt = lhsInt[0][1].integrand.subs(x1, -1.996794872)
//		.subs(x2, -1.786508761)
//		.subs(x3, -1.381601534)
//		.subs(y1,  1.996794872)
//		.subs(y2,  1.395640906)
//		.subs(y3,  1.747001174).simplify();
//		System.out.println(ttt);
//		Func fttt = new Func("fttt",ttt);
//		BytecodeFunc bfttt = fttt.toBytecodeFunc();
//		System.out.println(bfttt.apply());
	
		
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
		System.out.println("Start assemble the system...");
		long begin = System.currentTimeMillis();
		List<Domain> eles = mesh.getSubDomains();
		double[][] matA = new double[mesh.nodes.size()][mesh.nodes.size()];
		double[] vecb = new double[mesh.nodes.size()];
		for(Domain d : eles) {
			Element e = (Element)d;
			double[] nodeCoords = e.getNodeCoords();
			for(int i=0; i<shapeFuns.length; i++) {
				int idxI =  e.nodes.get(i).index-1;
				for(int j=0; j<shapeFuns.length; j++) {
					int idxJ = e.nodes.get(j).index-1;
					double t = lhsNInt[i][j].eval(nodeCoords);
					System.out.println(idxI+" "+idxJ+" "+t);
					
					matA[idxI][idxJ] += t;
				}
				vecb[idxI] += rhsNInt[i].eval(nodeCoords);
			}
		}
		System.out.println("Assemble done! Time: "+(System.currentTimeMillis()-begin)+"ms");
		
		System.out.println("Solving...");
		begin = System.currentTimeMillis();
		Matrix A = new Matrix(matA);
		Matrix b = new Matrix(vecb, vecb.length);
		if(dirichlet != null) {
			for(Node n : mesh.nodes) {
				Double diri = dirichlet.get(n.type);
				if(diri != null) {
					setDirichlet(A, b, n.index-1, diri);
				}
			}
		}
        for(int i=100; i<=110; i++) {
        	for(int j=100; j<=110; j++)
        		System.out.print(A.get(i-1, j-1)+" ");
        	System.out.println();
        }
        
		Matrix x = A.solve(b);
//		for(int i=0; i<x.getRowDimension(); i++)
//			System.out.println(x.get(i, 0));
		mesh.writeTechplot(output, x.getArray());
		System.out.println("Solved! Time: "+(System.currentTimeMillis()-begin)+"ms");
		System.out.println("See the output file(Tecplot format) "+output+" for the solution.");
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
