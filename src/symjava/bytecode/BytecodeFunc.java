package symjava.bytecode;

public interface BytecodeFunc {
	double apply(double ...args);
	default double call(double ...args) {
		return apply(args);
	}
}
