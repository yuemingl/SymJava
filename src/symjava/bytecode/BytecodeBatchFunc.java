package symjava.bytecode;

/**
 * Interface for function that return a list of values that is to say
 * the function evaluates a list of expressions with the same arguments
 *
 */
public interface BytecodeBatchFunc {
	void apply(double[] outAry, int outPos, double ...args);
}
