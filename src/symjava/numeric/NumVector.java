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
	
	public NumVector(SymVector sv, Expr[] args) {
		this.size = sv.dim();
		this.func = JIT.compile(args, sv.getData());
	}
	
	public int dim() {
		return this.size;
	}
	
	public double[] eval(double ...args) {
		lastEvalData = func.apply(args);
		return lastEvalData;
	}
	
	public double[] getData() {
		return lastEvalData;
	}
}
