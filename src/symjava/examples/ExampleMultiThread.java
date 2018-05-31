package symjava.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

/**
 * test class loader for multi thread case
 * @author yueming.liu
 *
 */
public class ExampleMultiThread {

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Expr R = 0.127-(x*0.194/(y+0.194));
				Expr Rdy = R.diff(y);
				System.out.println(Rdy);
				
				//Just-In-Time compile the symbolic expression to native code
				BytecodeFunc func = JIT.compile(new Expr[]{x,y}, Rdy);
				System.out.println(func.apply(0.362, 0.556)); //Scala function call operator
				System.out.println(func.call(0.362, 0.556)); //Groovy function call operator
				
			}
		});
		t.start();
		t.join();
	}

}
