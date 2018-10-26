package symjava.bytecode;

public class TestBytecodeVarArgs {
	public static int fun(double ...args) {
		return 0;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		fun(1.0,2.0);
		fun(1.0,2.0,3.0);
		TestBytecodeVarArgs[] ary = new TestBytecodeVarArgs[10];
	}

}
