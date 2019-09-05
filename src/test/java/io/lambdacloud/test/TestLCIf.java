package io.lambdacloud.test;

import io.lambdacloud.core.lang.LCDouble;
import io.lambdacloud.core.lang.LCIf;
import io.lambdacloud.core.lang.LCStatements;
import io.lambdacloud.core.lang.LCVar;
import io.lambdacloud.core.lang.LCReturn;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.relational.Gt;

public class TestLCIf {
	public static void main(String[] args) {
		LCVar x = new LCDouble("x");
		LCIf ifa = new LCIf(Gt.apply(x, 100));
		ifa.appendTrue(x.assign(x+100));
		ifa.appendTrue(x.assign(x+1));
		ifa.appendFalse(x.assign(x*100));
		//System.out.println(ifa);
		LCStatements s = new LCStatements();
		s.append(x.assign(101.0));
		//s.append(x.assign(10.0));
		s.append(ifa);
		s.append(new LCReturn(x));
		System.out.println(s);
		BytecodeFunc f = CompileUtils.compile(s);
		System.out.println(f.apply());
	}
}
