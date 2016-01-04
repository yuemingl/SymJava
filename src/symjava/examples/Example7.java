package symjava.examples;

import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Jama.Matrix;
import symjava.domains.Domain;
import symjava.examples.fem.*;
import symjava.math.Transformation;
import symjava.matrix.SymMatrix;
import symjava.numeric.NumFunc;
import symjava.numeric.NumInt;
import symjava.relational.Eq;
import symjava.symbolic.*;
import symjava.symbolic.utils.ExprPair;
import symjava.symbolic.utils.Utils;

/**
 * Finite Element solver
 * 
 */
public class Example7 {
	public static void main(String[] args) {
		Mesh2D mesh = new Mesh2D("S_3x3");
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
				double x = args[0]; //, y = args[1];
				if(3.0-x < eps)
					return 2; //
				return 0;
			}
			@Override
			public Expr[] args() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public TypeInfo getTypeInfo() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void updateLabel() {
				// TODO Auto-generated method stub
				
			}
		});
		
		WeakForm pde = new WeakForm(
					//Int.apply(1.0*dot(grad(u), grad(v))+sqrt(x*x+y*y)*u*v, mesh) + Int.apply((1.0*u-1.0)*v, neumannBC),
					//Int.apply(dot(grad(u), grad(v)), mesh) + Int.apply(u*v, neumannBC),
					//Int.apply(dot(grad(u), grad(v)), mesh),
					Integrate.apply(u.diff(x)*v.diff(x), mesh) + Integrate.apply(u.diff(y)*v.diff(y), mesh),
					Integrate.apply((-2*(x*x+y*y)+36)*v, mesh),
					u, v
				);
		
		//Mark boundary nodes
		for(Node n : mesh.nodes) {
			if(3.0-Math.abs(n.coords[1]) < eps || 3.0+n.coords[0] < eps ||
					3.0-n.coords[0] < eps)
				n.setType(1);
		}
		Map<Integer, Double> diri = new HashMap<Integer, Double>();
		diri.put(1, 0.0);

		//Solve
		solve(pde, diri, "triangle_neumann.dat");
		
	}
	
	public static void solve(WeakForm pde, Map<Integer, Double> dirichlet, String outputFile) {
		System.out.println(String.format("Solving: %s == %s", pde.lhs(), pde.rhs()));
		
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
		//Jacobian matrix of the transformation
		// jacMat = (xr xs)
		//          (yr ys)
		SymMatrix jacMat = trans.getJacobianMatrix();
		System.out.println(jacMat+"\n");
		
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

		//Create coordinate transformation for the boundary of a template element
		//r=[-1,1]
		Transformation transB = new Transformation(
				new Eq(x, x1*(1-r)/2.0+x2*(1+r)/2.0),
				new Eq(y, y1*(1-r)/2.0+y2*(1+r)/2.0)
				);
		//Shape functions on the bounary
		Func NB1 = new Func("RB");
		Func NB2 = new Func("SB", 1 - NB1);
		Func[] shapeFunsB = {NB1, NB2};
		Expr jacB = sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))/2.0; //transB.getJacobian();
		
		//Define reference elements
		RefTriangle refTri = new RefTriangle("RefT", r, s);
		RefLine refLine = new RefLine("RefL", r);
		
		
		Integrate lhsInt[][] = new Integrate[shapeFuns.length][shapeFuns.length];
		Integrate lhsIntB[][] = new Integrate[shapeFunsB.length][shapeFunsB.length];
		Integrate rhsInt[] = new Integrate[shapeFuns.length];
		

		Mesh2D mesh = null;
		Mesh2DBoundary meshB = null;
		
		List<Expr> addList = normalizeTerms(pde.lhs());
		//Change of variables
		for(Expr term : addList) {
			Integrate intTerm = (Integrate)term; // Integration term
			//Integrate on the domain
			if(intTerm.domain instanceof Mesh2D) {
				if(mesh == null)
					mesh = (Mesh2D) intTerm.domain;
				for(int i=0; i<shapeFuns.length; i++) {
					Func V = shapeFuns[i]; //test
					for(int j=0; j<shapeFuns.length; j++) {
						Func U = shapeFuns[j]; //trial
						//Create substitution list for change of variables
						List<ExprPair> subsList = new ArrayList<ExprPair>();
						//Substitute u, v to shape functions
						subsList.add(new ExprPair(pde.trial, U));
						subsList.add(new ExprPair(pde.test, V));
						subsList.add(new ExprPair(N1.diff(x), rx));
						subsList.add(new ExprPair(N1.diff(y), ry));
						subsList.add(new ExprPair(N2.diff(x), sx));
						subsList.add(new ExprPair(N2.diff(y), sy));
						subsList.add(new ExprPair(N1, r));
						subsList.add(new ExprPair(N2, s));
						subsList.add(new ExprPair(x, trans.eqs[0].rhs()));
						subsList.add(new ExprPair(y, trans.eqs[1].rhs()));
						lhsInt[i][j] = intTerm.changeOfVars(subsList, jac, refTri);
						lhsInt[i][j].integrand.setLabel("LHS"+i+j);
						System.out.println(lhsInt[i][j]+"\n");
					}
					List<ExprPair> subsList = new ArrayList<ExprPair>();
					subsList.add(new ExprPair(pde.test, V));
					subsList.add(new ExprPair(N1, r));
					subsList.add(new ExprPair(N2, s));
					subsList.add(new ExprPair(x, trans.eqs[0].rhs()));
					subsList.add(new ExprPair(y, trans.eqs[1].rhs()));
					rhsInt[i] = ((Integrate)pde.rhs()).changeOfVars(subsList, jac, refTri);
					rhsInt[i].integrand.setLabel("RHS"+i);
					System.out.println(rhsInt[i]+"\n");
				}
			//Integrate on the boundary of the domain	
			} else if(intTerm.domain instanceof Mesh2DBoundary) {
				if(meshB == null)
					meshB = (Mesh2DBoundary) intTerm.domain;
				for(int i=0; i<shapeFunsB.length; i++) {
					Func V = shapeFunsB[i]; //test
					for(int j=0; j<shapeFunsB.length; j++) {
						Func U = shapeFunsB[j]; //trial
						
						//Create substitution list for change of variables
						List<ExprPair> subsListB = new ArrayList<ExprPair>();
						//Substitute u, v to shape functions
						subsListB.add(new ExprPair(pde.trial, U));
						subsListB.add(new ExprPair(pde.test, V));
						subsListB.add(new ExprPair(NB1, r));
						subsListB.add(new ExprPair(NB2, s));
						subsListB.add(new ExprPair(x, transB.eqs[0].rhs()));
						subsListB.add(new ExprPair(y, transB.eqs[1].rhs()));
						lhsIntB[i][j] = intTerm.changeOfVars(subsListB, jacB, refLine);
						lhsIntB[i][j].integrand.setLabel("LHSB"+i+j);
						System.out.println(lhsIntB[i][j]+"\n");
					}
				}
			}
		}

		//Generate bytecode for the integration
		//You can save the class to some place for later use
		NumInt lhsNInt[][] = new NumInt[shapeFuns.length][shapeFuns.length];
		NumInt lhsNIntB[][] = new NumInt[shapeFunsB.length][shapeFunsB.length];
		NumInt rhsNInt[] = new NumInt[shapeFuns.length];
		for(int i=0; i<shapeFuns.length; i++) {
			for(int j=0; j<shapeFuns.length; j++) {
				lhsNInt[i][j] = new NumInt(lhsInt[i][j]);
			}
			rhsNInt[i] = new NumInt(rhsInt[i]);
		}
//		for(int i=0; i<shapeFunsB.length; i++) {
//			for(int j=0; j<shapeFunsB.length; j++) {
//				lhsNIntB[i][j] = new NumInt(lhsIntB[i][j]);
//			}
//		}

		//Assemble the system
		System.out.println("Start assemble the system...");
		long begin = System.currentTimeMillis();
		double[][] matA = new double[mesh.nodes.size()][mesh.nodes.size()];
		double[] vecb = new double[mesh.nodes.size()];
		for(Domain d : mesh.getSubDomains()) {
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
//		for(Domain db : meshB.getSubDomains()) {
//			Element eb = (Element)db;
//			double[] nodeCoords = eb.getNodeCoords();
//			for(int i=0; i<shapeFunsB.length; i++) {
//				int idxI =  eb.nodes.get(i).getIndex()-1;
//				for(int j=0; j<shapeFunsB.length; j++) {
//					int idxJ = eb.nodes.get(j).getIndex()-1;
//					double t = lhsNIntB[i][j].eval(nodeCoords);
//					//System.out.println(idxI+" "+idxJ+" "+t);
//					matA[idxI][idxJ] += t;
//				}
//			}
//		}
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
		Matrix x = A.solve(b);
//		for(int i=0; i<x.getRowDimension(); i++)
//			System.out.println(x.get(i, 0));
		mesh.writeTechplot(outputFile, x.getArray());
		System.out.println("Solved! Time: "+(System.currentTimeMillis()-begin)+"ms");
		System.out.println("See the output file(Tecplot format) "+outputFile+" for the solution.");
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
	 * Get a list of the integration terms in the equation and
	 * put the terms together according to the integration domains
	 * @param intTerms
	 * @return
	 */
	public static List<Expr> normalizeTerms(Expr intTerms) {
		List<Expr> addList = new ArrayList<Expr>();
		intTerms.flattenAdd(addList);
		Map<Domain, List<Expr>> map = new HashMap<Domain, List<Expr>>();
		for(int i=0; i<addList.size(); i++) {
			Integrate tmp = (Integrate)addList.get(i);
			List<Expr> list = map.get(tmp.domain);
			if(list == null) {
				list = new ArrayList<Expr>();
				map.put(tmp.domain, list);
			}
			list.add(tmp.integrand);
		}
		List<Expr> rlt = new ArrayList<Expr>();
		for(Entry<Domain, List<Expr>> entry : map.entrySet()) {
			rlt.add(
				Integrate.apply(Utils.addListToExpr(entry.getValue()), entry.getKey())
			);
		}
		return rlt;
	}
}
