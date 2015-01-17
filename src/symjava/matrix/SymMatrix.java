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
	
	public void print() {
		for(int i=0; i<data.size(); i++) {
			SymVector row = data.get(i);
			for(int j=0; j<row.dim(); j++)
				System.out.print(row.get(j)+"\t");
			System.out.println();
		}
	}
}
