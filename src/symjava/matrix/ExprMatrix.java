package symjava.matrix;

import java.util.Vector;

import symjava.numeric.NumMatrix;
import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;

/**
 * Matrix of symbolic expressions. 
 * 
 * See @Matrix for symbolic matrix which is a single
 * symbol represents a matrix object
 *
 */
public class ExprMatrix extends Expr {
	/**
	 * Row vectors
	 */
	Vector<ExprVector> data = new Vector<ExprVector>();;
	
	public ExprMatrix() {
	}
	
	public ExprMatrix(String name) {
	}
	
	public ExprMatrix(Expr[][] array) {
		for(Expr[] row : array)
			data.add(new ExprVector(row));
	}
	
	public ExprMatrix(int[][] array) {
		for(int[] row : array)
			data.add(new ExprVector(row));
	}
	
	public ExprMatrix(double[][] array) {
		for(double[] row : array)
			data.add(new ExprVector(row));
	}
	
	public ExprMatrix(int m, int n) {
		data.setSize(m);
		for(int i=0; i<data.size(); i++) {
			data.set(i, new ExprVector(n));
		}
	}
	
	public boolean isAbstract() {
		return data == null;
	}
	
	/**
	 * operator overload for m[i][j]
	 * @param i
	 * @return
	 */
	public ExprVector get(int i) {
		return data.get(i);
	}
	
	public Expr get(int i, int j) {
		return data.get(i).get(j);
	}
	
	public void set(int i, int j, Expr expr) {
		ExprVector row = data.get(i);
		row.set(j, expr);
	}
	
	public void append(ExprVector v) {
		data.add(v);
	}
	
	public int rowDim() {
		return data.size();
	}
	
	public int colDim() {
		if(data.size() > 0)
			return data.get(0).dim();
		return 0;
	}
	
//	  \left[ {\begin{array}{ccccc}
//	   1 & 2 & 3 & 4 & 5\\
//	   3 & 4 & 5 & 6 & 7\\
//	  \end{array} } \right]
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\\left[ {\\begin{array}{");
		for(int j=0; j<colDim(); j++)
			sb.append("c");
		sb.append("}\n");
		for(int i=0; i<data.size(); i++) {
			ExprVector row = data.get(i);
			sb.append(row.get(0));
			for(int j=1; j<row.dim(); j++)
				sb.append(" & "+row.get(j));
			sb.append("\\\\\n");
		}
		sb.append("\\end{array} } \\right]");
		return sb.toString();
	}
	
	public Expr det() {
		if(this.rowDim() == 1 && this.colDim() == 1) {
			return this.data.get(0).get(0);
		} else if(this.rowDim() == 2 && this.colDim() == 2) {
			Expr m11 = data.get(0).get(0);
			Expr m12 = data.get(0).get(1);
			Expr m21 = data.get(1).get(0);
			Expr m22 = data.get(1).get(1);
			return m11.multiply(m22).subtract(m12.multiply(m21));
		} else if(this.rowDim() == 3 && this.colDim() == 3) {
			Expr m11 = data.get(0).get(0);
			Expr m12 = data.get(0).get(1);
			Expr m13 = data.get(0).get(2);
			Expr m21 = data.get(1).get(0);
			Expr m22 = data.get(1).get(1);
			Expr m23 = data.get(1).get(2);
			Expr m31 = data.get(2).get(0);
			Expr m32 = data.get(2).get(1);
			Expr m33 = data.get(2).get(2);
			return m11.multiply( m22.multiply(m33).subtract(m23.multiply(m32)) ).subtract(
				   m12.multiply( m21.multiply(m33).subtract(m23.multiply(m31)) )).add(
				   m13.multiply( m21.multiply(m32).subtract(m22.multiply(m31)) ));
		}
		
		return null;
	}
	
//	public Expr norm2() {
//		
////		for(int j=0; j<this.colDim(); j++) {
////			for(int i=0; i<this.rowDim(); i++) {
////				Expr e = data.get(i).get(j);
////				e.multiply(e)
////			}
////		}
//	}
	
	public NumMatrix toNumMatrix(Expr[] args) {
		NumMatrix mat = new NumMatrix(this, args);
		return mat;
	}

	@Override
	public Expr add(Expr other) {
		ExprMatrix o = (ExprMatrix)other;
		ExprMatrix ret = new ExprMatrix();
		for(int i=0; i<data.size(); i++) {
			ret.append((ExprVector)(this.get(i).add(o.get(i))));
		}
		return ret;
	}
	
	@Override
	public Expr multiply(Expr other) {
		if(other instanceof ExprVector) {
			ExprVector ret = new ExprVector();
			for(int i=0; i<this.rowDim(); i++) {
				ret.append(this.get(i).dot((ExprVector)other));
			}
			return ret;
		} else if(other instanceof ExprMatrix) {
			//TODO
		} 
		return null;
	}
	
	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
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
}
