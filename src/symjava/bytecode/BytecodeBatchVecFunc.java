package symjava.bytecode;

public class BytecodeBatchVecFunc implements BytecodeVecFunc {
	BytecodeVecFunc func;
	int vecLen; //Input vector length
	int retLen; //Return vector length
	
	public BytecodeBatchVecFunc(BytecodeVecFunc func, int vecLen, int retLen) {
		this.func = func;
		this.vecLen = vecLen;
		this.retLen = retLen;
	}

	@Override
	public void apply(double[] outAry, int outPos, double[]... args) {
		double[] tmpBuf = new double[retLen];
		double[][] tmpArgs = new double[args.length][vecLen];
		int destPos = 0;
		int argPos = 0;
		for(int i=0; i<args[0].length/vecLen; i++) {
			for(int j=0; j<args.length; j++) {
				System.arraycopy(args[j], argPos, tmpArgs[j], 0, vecLen);
			}
			func.apply(tmpBuf, 0, tmpArgs);
			System.arraycopy(tmpBuf, 0, outAry, destPos, retLen);
			destPos += retLen;
			argPos += vecLen;
		}
	}
}