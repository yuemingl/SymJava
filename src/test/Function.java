package test;

public interface Function {
	default double apply() {
		throw new RuntimeException("0 parameter");
	}
	
	default double apply(double x) {
		throw new RuntimeException("1 parameter");
	}
	
	default double apply(double x, double y) {
		throw new RuntimeException("2 parameters");
		
	}
	
	default double apply(double ...x) {
		throw new RuntimeException("N parameters");
	}
	
	
	static public Function create(Fun f) {
		return new Function() {
			public double apply(double ...x) {
				return f.apply(x);
			}
		};
	}
	
	static public Function create(Fun2 f) {
		return new Function() {
			public double apply(double x, double y) {
				return f.apply(x, y);
			}
		};
	}
}
