package lambdacloud.examples;

import static lambdacloud.core.LambdaCloud.CPU;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.Session;
import lambdacloud.core.lang.LCDevice;
import symjava.symbolic.Expr;

/**
 * Different ways to compute x*x + y*y on two devices
 * 
 *
 */
public class Example4 {

	public static void main(String[] args) {
		Session sess = new Session();
		Map<String, Double> dict = new HashMap<String, Double>();
		dict.put(x.toString(), 3.0);
		dict.put(y.toString(), 4.0);

		//Set device in the expression
		Expr sum1 = CPU(x*x) + CPU(y*y);
		double rlt1 = sess.runSimple(sum1, dict);
		System.out.println("Example 1: "+sum1);
		System.out.println("Result: "+rlt1);
		
		//use runOn() method of a term or an expression
		LCDevice cpu1 = new LCDevice("0");
		LCDevice cpu2 = new LCDevice("1");
		Expr sum2 = (x*x).runOn(cpu1) + (y*y).runOn(cpu2);
		double rlt2 = sess.runSimple(sum1, dict);
		System.out.println("Example 2: "+sum2);
		System.out.println("Result: "+rlt2);
		
		//Set device for each term of an expression (use args())
		Expr sum3 = x*x + y*y;
		Expr[] ops = sum3.args();
		for(int i=0; i<ops.length; i++) {
			ops[i].runOn(new LCDevice(i));
		}
		double rlt3 = sess.runSimple(sum1, dict);
		System.out.println("Example 3: "+sum2);
		System.out.println("Result: "+rlt3);
	}

}
