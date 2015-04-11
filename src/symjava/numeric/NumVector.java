package symjava.numeric;

import symjava.bytecode.BytecodeVecFunc;
import symjava.matrix.SymVector;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class NumVector {
	BytecodeVecFunc func;
	int size;
	double[] lastEvalData;
	
	public NumVector() {
		
	}
	
	public NumVector(int size) {
		this.size = size;
	}
	
	public NumVector(JIT jit, SymVector sv, Expr[] args) {
		this.size = sv.dim();
		this.func = jit.compile(args, sv.getData());
	}
	
	public int dim() {
		return this.size;
	}
	
	public double[] eval(double[] outAry, double ...args) {
		func.apply(outAry, 0, args);
		this.lastEvalData = outAry;
		return lastEvalData;
	}
	
	public double[] getData() {
		return lastEvalData;
	}
}

