package symjava.matrix;

import java.util.Vector;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Symbol;

public class NumVector {
	Vector<BytecodeFunc> data = new Vector<BytecodeFunc>();
	
	public NumVector() {
		
	}
	
	public NumVector(int size) {
		data.setSize(size);
	}
	
	public NumVector(SymVector sv, Symbol[] args) {
		int n = sv.dim();
		data.setSize(n);
		for(int j=0; j<n; j++) {
			Expr e = sv.get(j);
			if(e == null)
				continue;
			Func func = null;
			if(sv.get(j) instanceof Func) {
				func = (Func)e; 
			} else {
				func = new Func(this.getClass().getSimpleName()+java.util.UUID.randomUUID().toString().replaceAll("-", "")+"_"+j,e);
				func.args = args;
				//System.out.println(func.getLabel());
			}
			data.set(j, func.toBytecodeFunc());
		}
	}
	
	public BytecodeFunc get(int i) {
		return data.get(i);
	}
	
	public void set(int i, BytecodeFunc func) {
		data.set(i, func);
	}
	
	public void add(BytecodeFunc e) {
		data.add(e);
	}
	
	public int dim() {
		return data.size();
	}
	
	public double[] eval(double ...args) {
		int m = dim();
		double[] rlt = new double[m];
		for(int i=0; i<m; i++) {
			rlt[i] = data.get(i).apply(args);
		}
		return rlt;
	}	
}
