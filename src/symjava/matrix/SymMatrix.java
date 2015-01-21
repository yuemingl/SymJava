package symjava.matrix;

import java.util.Vector;

import symjava.symbolic.Expr;

public class SymMatrix {
	Vector<SymVector> data = new Vector<SymVector>();
	public SymMatrix() {
	}
	
	public SymMatrix(int m, int n) {
		data.setSize(m);
		for(int i=0; i<data.size(); i++) {
			data.set(i, new SymVector(n));
		}
	}
	
	public SymVector get(int i) {
		return data.get(i);
	}
	
	public Expr get(int i, int j) {
		return data.get(i).get(j);
	}
	
	public void set(int i, int j, Expr expr) {
		SymVector row = data.get(i);
		row.set(j, expr);
	}
	
	public void add(SymVector v) {
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
			SymVector row = data.get(i);
			sb.append(row.get(0));
			for(int j=1; j<row.dim(); j++)
				sb.append(" & "+row.get(j));
			sb.append("\\\\\n");
		}
		sb.append("\\end{array} } \\right]");
		return sb.toString();
	}
}
