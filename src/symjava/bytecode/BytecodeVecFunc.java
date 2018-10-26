package symjava.bytecode;

/**
 * Interface for function with vector valued arguments and return a vector
 *
 */
public interface BytecodeVecFunc {
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
