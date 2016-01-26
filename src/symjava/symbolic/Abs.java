package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Abs extends UnaryOp {

	public Abs(Expr arg) {
		super(arg);
		label = "|" + arg + "|";
		sortKey = label;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Abs) {
			Utils.symCompare(this.arg, ((Cos) other).arg);
		}
		return false;
	}
	
	public static Expr simplifiedIns(Expr expr) {
		return new Abs(expr);
	}

	/**
	 * Recall that |f(x)| = sqrt(f(x)*f(x)), so
	 * |f(x)|' = f(x)*f'(x)/|f(x)|
	 */
	@Override
	public Expr diff(Expr x) {
		return arg.multiply(arg.diff(x)).divide(this);
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg.getType() == TYPE.MATRIX || arg.getType() == TYPE.VECTOR) {
			il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "abs",
					new ObjectType("Jama.Matrix"), 
					new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKESTATIC));
		} else {
			il.append(factory.createInvoke("java.lang.Math", "abs",
					Type.DOUBLE, 
					new Type[] { Type.DOUBLE },
					Constants.INVOKESTATIC));
		}
		return startPos;
	}

	@Override
	public void updateLabel() {
		label = "|" + arg + "|";
		sortKey = label;
	}
}
