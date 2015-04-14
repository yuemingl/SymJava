package symjava.matrix;

import java.util.Iterator;
import java.util.Vector;

import symjava.numeric.NumVector;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.JIT;

public class SymVector implements Iterable<Expr> {
	protected Vector<Expr> data = new Vector<Expr>();
	
	public SymVector() {
	}
	
	public SymVector(int size) {
		data.setSize(size);
	}
	
	public SymVector(Expr[] array) {
		for(Expr e : array)
			data.add(e);
	}
	
	public SymVector(Expr[] array, int startPos, int length) {
		for(int i=startPos; i<startPos+length; i++)
			data.add(array[i]);
	}

	public SymVector(double[] array) {
		for(double e : array)
			data.add(Expr.valueOf(e));
	}
	
	public SymVector(double[] array, int startPos, int length) {
		for(int i=startPos; i<startPos+length; i++)
			data.add(Expr.valueOf(array[i]));
	}
	
	public SymVector(String prefix, int startIdx, int endIdx) {
		Symbols v = new Symbols(prefix);
		for(Expr e : v.get(startIdx, endIdx))
			data.add(e);
	}
	
	public Expr get(int i) {
		return data.get(i);
	}
	
	public void set(int i, Expr expr) {
		if(i >= data.size())
			data.setSize(i+1);
		data.set(i, expr);
	}
	
	public void add(Expr e) {
		data.add(e);
	}
	
	public int dim() {
		return data.size();
	}
	
	public int length() {
		return data.size();
	}
	
	public Expr[] getData() {
		return data.toArray(new Expr[0]);
	}
	
//	  \left[ {\begin{array}{c}
//	   1\\
//	   3\\
//	  \end{array} } \right]
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\\left[ {\\begin{array}{c}");
		for(int j=0; j<data.size(); j++) {
			sb.append(data.get(j)+"\\\\\n");
		}
		sb.append("\\end{array} } \\right]");
		return sb.toString();
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
	
	public SymVector diff(Expr expr) {
		SymVector rlt = new SymVector();
		for(Expr e : data) {
			rlt.add(e.diff(expr));
		}
		return rlt;
	}
	
	public NumVector toNumVector(Expr[] args) {
		NumVector ret = new NumVector(this, args);
		return ret;
	}
}
