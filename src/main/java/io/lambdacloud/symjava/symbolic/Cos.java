package io.lambdacloud.symjava.symbolic;

import java.util.Map;

import io.lambdacloud.symjava.symbolic.Expr.TYPE;
import io.lambdacloud.symjava.symbolic.arity.UnaryOp;
import io.lambdacloud.symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Cos extends UnaryOp {
	public Cos(Expr arg) {
		super(arg);
		updateLabel();
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
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		Expr sl = arg.subs(from, to);
		if(sl == arg)
			return this;
		return new Cos(sl);
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
		if(arg.getType() == TYPE.MATRIX || arg.getType() == TYPE.VECTOR) {
			il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "cos",
					new ObjectType("Jama.Matrix"), 
					new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKESTATIC));
		} else {
			il.append(factory.createInvoke("java.lang.Math", "cos",
					Type.DOUBLE, 
					new Type[] { Type.DOUBLE },
					Constants.INVOKESTATIC));
		}
		
		return startPos;
	}

	@Override
	public void updateLabel() {
		label = "cos(" + arg + ")";
		sortKey = label;
	}
}
