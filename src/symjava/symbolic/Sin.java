package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Sin extends UnaryOp {

	public Sin(Expr arg) {
		super(arg);
		label = "sin(" + arg + ")";
		sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		return new Cos(arg).multiply(arg.diff(expr));
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Sin(expr);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sin) {
			return Utils.symCompare(this.arg, ((Sin) other).arg);
		}
		return false;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(factory.createInvoke("java.lang.Math", "sin",
				Type.DOUBLE, 
				new Type[] { Type.DOUBLE },
		Constants.INVOKESTATIC));
		return startPos;
	}
}
