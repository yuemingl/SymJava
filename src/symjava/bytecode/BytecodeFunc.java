package symjava.bytecode;

/**
 * Interface for function with zero or multiple arguments and return exactly one value
 * For example:
 * f(x)   = x*x + 1
 * f(x,y) = sqrt(x*x + y*y)
 *
 */
public interface BytecodeFunc {
	double apply(double ...args);
}
