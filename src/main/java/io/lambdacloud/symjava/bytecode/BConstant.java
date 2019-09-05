package io.lambdacloud.symjava.bytecode;

public class BConstant implements BytecodeFunc {
	double value;
	public BConstant(double v) {
		this.value = v;
	}
	@Override
	public double apply(double... args) {
		return value;
	}

}
