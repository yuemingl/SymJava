package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * An object of SymConst represent a mathematical constant such as PI, E.
 * The constant is displayed as its label but used as a double number 
 * in numerical computation.
 * 
 */
public class SymConst extends Expr {
	double value;
	
	public SymConst(String label, double value) {
		this.label = label;
		this.sortKey = label;
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public Expr diff(Expr expr) {
		return Symbol.C0;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return this.label.equals(other.label);
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		return il.append(new PUSH(cp, value));
	}	
}
