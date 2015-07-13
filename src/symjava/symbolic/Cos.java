package symjava.symbolic;

import java.util.Map;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Cos extends UnaryOp {
	public Cos(Expr arg) {
		super(arg);
		label = "cos(" + arg + ")";
		sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		return Negate.simplifiedIns(Sin.simplifiedIns(arg)).multiply(arg.diff(expr));
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Cos(expr);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Cos) {
			Utils.symCompare(this.arg, ((Cos) other).arg);
		}
		return false;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(factory.createInvoke("java.lang.Math", "cos",
				Type.DOUBLE, 
				new Type[] { Type.DOUBLE },
		Constants.INVOKESTATIC));
		return startPos;
	}
}
