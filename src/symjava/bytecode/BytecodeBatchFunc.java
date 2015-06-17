package symjava.bytecode;

public interface BytecodeBatchFunc {
	/**
	 * Take arguments from 'args' and return the results to 'outAry'.
	 * The first number is stored in 'outAry' starting from 'outPos'.
	 *  
	 * @param outAry
	 * @param outPos
	 * @param args
	 */
	void apply(double[] outAry, int outPos, double[] ...args);
}
