package test;

public class LambdaTest {

	public static double myfun(double x, double y) {
		return 0;
	}
	
	public Double myfun2() {
		return 0.0;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//BiFunction b;
		//Iterable it;
		
		String s = "ddddd";
		
		Fun f = x -> {
			System.out.println(s);
			return x[0] * x[1] * x[2];
		};
		
		System.out.println( f.apply(1,2,3) );

		Fun2 f2 = (x, y) -> x+y;
		System.out.println( f2.apply(1.0,2.0) );
		
		Function myFun = Function.create(f);
		Function myFun2 = Function.create(f2);
		
		System.out.println(myFun.apply(1,2,3));
		
		System.out.println(myFun2.apply(1,2));
		
		Fun2 f22 = LambdaTest::myfun;
		
		java.util.function.Function<LambdaTest, Double> tt = LambdaTest::myfun2;
		LambdaTest a = new LambdaTest();
		System.out.println(tt.apply(a));
		
	}

}

