package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

/**
 * i++
 *
 */
public class LCInc extends LCBase {
	LCInt var;
	int inc;
	public LCInc(LCInt var) {
		this.var = var;
		this.inc = 1;
		updateLabel();
	}
	
	public LCInc(LCInt var, int increment) {
		this.var = var;
		this.inc = increment;
		updateLabel();
	}
	
	public void updateLabel() {
		if(inc == 1)
			this.label = this.indent + this.var+"++";
		else
			this.label = this.indent + this.var+"+=" + this.inc;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		return il.append(new IINC(var.getLVTIndex(), inc));
	}
}
