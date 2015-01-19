package symjava.matrix;

import java.util.Vector;

import symjava.symbolic.Expr;

public class SymVector {
	Vector<Expr> data = new Vector<Expr>();
	
	public SymVector() {
		
	}
	
	public SymVector(int size) {
		data.setSize(size);
	}
	
	public Expr get(int i) {
		return data.get(i);
	}
	
	public void set(int i, Expr expr) {
		data.set(i, expr);
	}
	
	public void add(Expr e) {
		data.add(e);
	}
	
	public int dim() {
		return data.size();
	}
	
	public void print() {
		for(int j=0; j<data.size(); j++) {
			System.out.println(data.get(j));
		}
	}
}
