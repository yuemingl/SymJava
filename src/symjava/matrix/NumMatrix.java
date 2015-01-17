package symjava.matrix;

import java.util.Vector;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Expr;

public class NumMatrix {
	Vector<Vector<BytecodeFunc>> data = new Vector<Vector<BytecodeFunc>>();
	
	public NumMatrix() {
	}
	
	public NumMatrix(int m, int n) {
		data.setSize(m);
		for(int i=0; i<m; i++) {
			data.set(i, new Vector<BytecodeFunc>(n));
		}
	}
	
	public NumMatrix(SymMatrix sm, Expr[] args) {
		int m = sm.rowDim();
		int n = sm.colDim();
		data.setSize(m);
		for(int i=0; i<m; i++) {
			Vector<BytecodeFunc> row = new Vector<BytecodeFunc>();
			row.setSize(n);
			data.set(i, row);
			for(int j=0; j<n; j++) {
				Expr e = sm.get(i, j);
				if(e == null)
					continue;
				Func func = null;
				if(sm.get(i, j) instanceof Func) {
					func = (Func)e; 
				} else {
					func = new Func(this.getClass().getSimpleName()+java.util.UUID.randomUUID().toString().replaceAll("-", "")+"_"+i+"_"+j,e);
					func.args = args;
					//System.out.println(func.getLabel());
				}
				row.set(j, func.toBytecodeFunc());
			}
		}
	}
	
	public BytecodeFunc get(int i, int j) {
		return data.get(i).get(j);
	}
	
	public void set(int i, int j, BytecodeFunc func) {
		Vector<BytecodeFunc> row = data.get(i);
		row.set(j, func);
	}
	
	public void add(NumVector v) {
		data.add(v.data);
	}
	
	public int rowDim() {
		return data.size();
	}
	
	public int colDim() {
		if(data.size() > 0)
			return data.get(0).size();
		return 0;
	}
	
	public double[][] eval(double ...args) {
		int m = rowDim();
		int n = colDim();
		double[][] rlt = new double[m][n];
		for(int i=0; i<m; i++) {
			for(int j=0; j<n; j++) {
				rlt[i][j] = this.get(i, j).apply(args);
			}
		}
		return rlt;
	}
}
