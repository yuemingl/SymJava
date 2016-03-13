package lambdacloud.test;

import static symjava.symbolic.Symbol.*;
import static lambdacloud.test.TestUtils.*;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCVar;

public class TestCloudFunc {
	public static void main(String[] args) {
		CloudConfig.setGlobalTarget("job_local.conf");

		testConstructors1();
		testConstructors2();
		testConstructors3();
		testConstructors4();
		
		testLocalVar1();
		testLocalVar2();
		
		
	}
	
	public static void testConstructors1() {
		CloudFunc func = new CloudFunc(x + y);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{3}, output.getData());
	}
	public static void testConstructors2() {
		CloudFunc func = new CloudFunc(x + y, x, y);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{3}, output.getData());
	}
	public static void testConstructors3() {
		CloudFunc func = new CloudFunc("func_test1",x + y, x, y);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{3}, output.getData());
	}
	public static void testConstructors4() {
		CloudFunc func = new CloudFunc("func_test2",x + y, x, y);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{3}, output.getData());
	}
	
	public static void testLocalVar1() {
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");
		CloudFunc func = new CloudFunc(x + y);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{0}, output.getData());
	}

	public static void testLocalVar2() {
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");
		CloudFunc func = new CloudFunc(x + y, new LCVar[]{x,y});
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2});
		func.apply(output, input);
		output.fetch();
		assertEqual(new double[]{3}, output.getData());
	}

	
}
