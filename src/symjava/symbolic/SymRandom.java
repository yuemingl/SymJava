package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Represents a random number in the range [0.0, 1.0]
 *
 */
public class SymRandom extends Expr {
	public SymRandom() {
		this.label = "random()";
		this.sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		return null;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiDouble;
	}
	
	@Override
	public Expr[] args() {
		return new Expr[0];
	}	
	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		return il.append(factory.createInvoke("java.lang.Math", "random",
				Type.DOUBLE, new Type[] { }, Constants.INVOKESTATIC));
	}

	@Override
	public void updateLabel() {
	}	
}
