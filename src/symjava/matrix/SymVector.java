package symjava.matrix;

import java.util.Iterator;
import java.util.Vector;

import symjava.symbolic.Expr;

public class SymVector implements Iterable<Expr> {
	protected Vector<Expr> data = new Vector<Expr>();
	
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
	
//	  \left[ {\begin{array}{c}
//	   1\\
//	   3\\
//	  \end{array} } \right]
	public void print() {
		StringBuilder sb = new StringBuilder();
		sb.append("\\left[ {\\begin{array}{c}");
		for(int j=0; j<data.size(); j++) {
			sb.append(data.get(j)+"\\\\\n");
		}
		sb.append("\\end{array} } \\right]");
		System.out.println(sb.toString());
	}

	@Override
	public Iterator<Expr> iterator() {
		return data.iterator();
	}
	
	public SymVector subs(Expr from, Expr to) {
		SymVector rlt = new SymVector();
		for(Expr e : data) {
			rlt.add(e.subs(from, to));
		}
		return rlt;
	}
}
