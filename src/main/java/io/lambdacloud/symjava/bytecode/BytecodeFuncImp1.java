package io.lambdacloud.symjava.bytecode;

public class BytecodeFuncImp1 implements BytecodeFunc {
	@Override
	public double apply(double... args) {
		return args[0]+args[1];
	}
}
