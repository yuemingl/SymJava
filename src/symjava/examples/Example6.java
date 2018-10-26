package symjava.examples;

import static symjava.math.SymMath.dot;
import static symjava.math.SymMath.grad;
import static symjava.symbolic.Symbol.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import symjava.domains.Domain;
import symjava.examples.fem.Element;
import symjava.examples.fem.Mesh2D;
import symjava.examples.fem.Node;
import symjava.examples.fem.RefTriangle;
import symjava.examples.fem.WeakForm;
import symjava.math.Transformation;
import symjava.matrix.ExprMatrix;
import symjava.numeric.NumInt;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Integrate;
import symjava.symbolic.SymConst;
import symjava.symbolic.Symbol;

/**
 * Finite Element solver
 * 
 */
public class Example6 {
	public static void main(String[] args) {
//		Func u = new Func("u", x, y);
//		Func v = new Func("v", x, y);
//
////		//Our PDE equation
////		Eq pde = new Eq(0.5*dot(grad(u), grad(v)) + 0.1*u*v, (x*x+y*y)*v);
////		//Read the mesh
////		Mesh2D mesh = new Mesh2D("mesh1", x, y);
////		mesh.readTriangleMesh("double_hex3.1.node", "double_hex3.1.ele");
////		solve(pde, mesh, null, "double_hex3.1.dat");
//
//		//Another PDE equation with Dirichlet condition
//		WeakForm wf = new WeakForm(dot(grad(u), grad(v)), (-2*(x*x+y*y)+36)*v, u, v);
//		//Eq pde2 = new Eq(u*v, (-2*(x*x+y*y)+36)*v);
//		Mesh2D mesh2 = new Mesh2D("mesh2");
//		//mesh2.readGridGenMesh("patch_triangle.grd");
//		mesh2.readGridGenMesh("triangle.grd");
//		//Mark boundary nodes
//		double eps = 0.01;
//		for(Node n : mesh2.nodes) {
//			//if(1-Math.abs(n.coords[0])<eps || 1-Math.abs(n.coords[1])<eps || Math.abs(n.coords[0])<eps || Math.abs(n.coords[1])<eps )
//			if(Math.abs(3-Math.abs(n.coords[0]))<eps || Math.abs(3-Math.abs(n.coords[1]))<eps)
//				n.setType(1);
//		}
//		Map<Integer, Double> diri = new HashMap<Integer, Double>();
//		diri.put(1, 0.0);
//		//solve(pde2, mesh2, diri, "patch_triangle.dat");
//		solve(wf, mesh2, diri, "triangle.dat");
//		solve2(mesh2, diri, "triangle_hardcode.dat");
		peper_example();
	}
	
	public static void peper_example() {
Func u = new Func("u", x, y);
Func v = new Func("v", x, y);
WeakForm wf = new WeakForm(dot(grad(u), grad(v)), (-2*(x*x+y*y)+36)*v, u, v);
Mesh2D mesh = new Mesh2D("Square");
mesh.readGridGenMesh("triangle.grd");
//Mark boundary nodes
double eps = 0.01;
for(Node n : mesh.nodes) {
	if(Math.abs(3-Math.abs(n.coords[0]))<eps || 
			Math.abs(3-Math.abs(n.coords[1]))<eps)
		n.setType(1);
}
Map<Integer, Double> diri = new HashMap<Integer, Double>();
diri.put(1, 0.0);
solve(wf, mesh, diri, "triangle.dat");
	}
	public static void solve(WeakForm wf, Mesh2D mesh, Map<Integer, Double> dirichlet, String output) {
		System.out.println(String.format("PDE Weak Form: %s = %s", wf.lhs(), wf.rhs()));
		
		//Create coordinate transformation
		Symbol x1 = new Symbol("x1");
		Symbol x2 = new Symbol("x2");
		Symbol x3 = new Symbol("x3");
		Symbol y1 = new Symbol("y1");
		Symbol y2 = new Symbol("y2");
		Symbol y3 = new Symbol("y3");
		Transformation trans = new Transformation(
				new Eq(x, x1*r+x2*s+x3*(1-r-s), new Expr[]{r, s}),
				new Eq(y, y1*r+y2*s+y3*(1-r-s), new Expr[]{r, s})
				);
		// jac = (xr xs)
		//       (yr ys)
		ExprMatrix jacMat = trans.getJacobianMatrix();
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

		RefTriangle tri = new RefTriangle("Tri", r, s);
		Integrate lhsInt[][] = new Integrate[shapeFuns.length][shapeFuns.length];
		Integrate rhsInt[] = new Integrate[shapeFuns.length];
		for(int i=0; i<shapeFuns.length; i++) {
			Func V = shapeFuns[i]; //test
			for(int j=0; j<shapeFuns.length; j++) {
				Func U = shapeFuns[j]; //trial
				
				//Weak form for the left hand side of the PDE
				Expr lhs = wf.lhs().subs(wf.trial, U).subs(wf.test, V);
				System.out.println(lhs);
				System.out.println();
				
				//Replace the derivatives with it's concrete expression
				lhs = lhs.subs(N1.diff(x), rx)
						.subs(N1.diff(y), ry)
						.subs(N2.diff(x), sx)
						.subs(N2.diff(y), sy)
						.subs(N1, r).subs(N2, s)
						.subs(x, trans.eqs[0].rhs())
						.subs(y, trans.eqs[1].rhs());
				System.out.println(lhs);
				System.out.println();
				
				//Define the integration on the reference domain
				//lhsInt[i][j] = new Int(new Func("",lhs*jac,new Expr[]{r,s}), tri);
				lhsInt[i][j] = new Integrate(new Func(
						String.format("lhs%d%d",i,j), lhs*jac,new Expr[]{r,s}), tri);
				System.out.println(lhsInt[i][j]);
				System.out.println();
			}
			//Weak form for the right hand side of the PDE
			Expr rhs = wf.rhs().subs(wf.test, V)
							.subs(N1, r).subs(N2, s)
							.subs(x, trans.eqs[0].rhs())
							.subs(y, trans.eqs[1].rhs());
			//System.out.println(rhs);
			System.out.println();
			rhsInt[i] = new Integrate(new Func(
					String.format("rhs%d",i),rhs*jac,new Expr[]{r,s}), tri);
		}
		
//		Expr ttt = lhsInt[0][1].integrand
//		.subs(x1, -1.996794872)
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
				int idxI =  e.nodes.get(i).getIndex()-1;
				for(int j=0; j<shapeFuns.length; j++) {
					int idxJ = e.nodes.get(j).getIndex()-1;
					double t = lhsNInt[i][j].eval(nodeCoords);
					//System.out.println(idxI+" "+idxJ+" "+t);
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
				Double diri = dirichlet.get(n.getType());
				if(diri != null) {
					setDirichlet(A, b, n.getIndex()-1, diri);
				}
			}
		}
//        for(int i=100; i<=110; i++) {
//        	for(int j=100; j<=110; j++)
//        		System.out.print(A.get(i-1, j-1)+" ");
//        	System.out.println();
//        }
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

	/**
	 * Hard code the solver for
	 * 
	 * dot(grad(u), grad(v)) == (-2*(x*x+y*y)+36)*v
	 * 
	 * @param mesh
	 * @param dirichlet
	 * @param output
	 */
	public static void solve2(Mesh2D mesh, Map<Integer, Double> dirichlet, String output) {
		//Assemble the system
		System.out.println("Start assemble the system...");
		long begin = System.currentTimeMillis();
		List<Domain> eles = mesh.getSubDomains();
		double[][] matA = new double[mesh.nodes.size()][mesh.nodes.size()];
		double[] vecb = new double[mesh.nodes.size()];
		for(Domain d : eles) {
			Element e = (Element)d;
			double[] coords = e.getNodeCoords();
			double x1 = coords[0];
			double x2 = coords[1];
			double x3 = coords[2];
			double y1 = coords[3];
			double y2 = coords[4];
			double y3 = coords[5];
			//x = x1*r+x2*s+x3*(1-r-s)
			//y = y1*r+y2*s+y3*(1-r-s)
			double xr = x1-x3;
			double xs = x2-x3;
			double yr = y1-y3;
			double ys = y2-y3;
			// jac = (xr xs)
			//       (yr ys)
			double jac = xr*ys-xs*yr;
			double rx =  ys/jac;
			double ry = -xs/jac;
			double sx = -yr/jac;
			double sy =  xr/jac;
			double tx = -rx - sx;
			double ty = -ry - sy;
			
			double[][] lhs = {
					{rx*rx + ry*ry, rx*sx + ry*sy, rx*tx+ry*ty},
					{sx*rx + sy*ry, sx*sx + sy*sy, sx*tx+sy*ty},
					{tx*rx + ty*ry, tx*sx + ty*sy, tx*tx+ty*ty},
			};
			for(int i=0; i<3; i++) {
				int idxI =  e.nodes.get(i).getIndex()-1;
				for(int j=0; j<3; j++) {
					int idxJ = e.nodes.get(j).getIndex()-1;
					double t = lhs[i][j]*jac*0.5;
					matA[idxI][idxJ] += t;
				}
			}

			double[][] intPnW = {
					{0.5, 0.5, 1.0/6.0},
					{0.0, 0.5, 1.0/6.0},
					{0.5, 0.0, 1.0/6.0}
			};
			for(int k=0; k<intPnW.length; k++) {
				double r=intPnW[k][0];
				double s=intPnW[k][1];
				double t=1-r-s;
				double w=intPnW[k][2];
				double x = x1*r+x2*s+x3*t;
				double y = y1*r+y2*s+y3*t;
				//f=-2*(x*x+y*y)+36
				double f = -2.0*(x*x+y*y)+36.0;
				vecb[e.nodes.get(0).getIndex() - 1] += f*r*jac*w;
				vecb[e.nodes.get(1).getIndex() - 1] += f*s*jac*w;
				vecb[e.nodes.get(2).getIndex() - 1] += f*t*jac*w;
			}

		}
		System.out.println("Assemble done! Time: "+(System.currentTimeMillis()-begin)+"ms");
		
		System.out.println("Solving...");
		begin = System.currentTimeMillis();
		Matrix A = new Matrix(matA);
		Matrix b = new Matrix(vecb, vecb.length);
		if(dirichlet != null) {
			for(Node n : mesh.nodes) {
				Double diri = dirichlet.get(n.getType());
				if(diri != null) {
					setDirichlet(A, b, n.getIndex()-1, diri);
				}
			}
		}
//        for(int i=100; i<=110; i++) {
//        	for(int j=100; j<=110; j++)
//        		System.out.print(A.get(i-1, j-1)+" ");
//        	System.out.println();
//        }
//		for(int i=100; i<=110; i++) {
//			System.out.println(b.get(i-1, 0));
//		}
		/*
3.678403159123969 -0.9973611181090696 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 
-0.9973611181090696 3.877353541972138 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 
0.0 0.0 3.525365130978284 -0.4486227282431723 -0.393018578918151 0.0 0.0 0.0 0.0 -0.24553903148664002 0.0 
0.0 0.0 -0.4486227282431723 3.5372167175010087 -0.8194573851545361 0.0 0.0 0.0 0.0 0.0 0.0 
0.0 0.0 -0.393018578918151 -0.8194573851545361 3.535701918507291 0.0 0.0 0.0 0.0 0.0 0.0 
0.0 0.0 0.0 0.0 0.0 3.4945816438189032 -0.6132348151677102 -0.22191378338605314 0.0 0.0 0.0 
0.0 0.0 0.0 0.0 0.0 -0.6132348151677102 3.707317244324667 -0.8671907304126691 0.0 0.0 0.0 
0.0 0.0 0.0 0.0 0.0 -0.22191378338605314 -0.8671907304126691 3.4758034494659844 0.0 0.0 0.0 
0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 3.623619648286562 0.0 0.0 
0.0 0.0 -0.24553903148664002 0.0 0.0 0.0 0.0 0.0 0.0 3.501878694448894 -0.4577721650038804 
0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.4577721650038804 3.6326693760208175 
5.920480649794939
5.830543653385974
11.166016717821526
9.51298710892804
7.987159886596924
9.00295976201226
6.471980478636926
9.623214010483725
6.664903619294702
10.961199858227298
7.935696861306976
		 */
		Matrix x = A.solve(b);
		mesh.writeTechplot(output, x.getArray());
		System.out.println("Solved! Time: "+(System.currentTimeMillis()-begin)+"ms");
		System.out.println("See the output file(Tecplot format) "+output+" for the solution.");
	}	
}
