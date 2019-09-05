package io.lambdacloud.symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import io.lambdacloud.symjava.symbolic.arity.UnaryOp;
import io.lambdacloud.symjava.symbolic.utils.Utils;

public class Sin extends UnaryOp {

	public Sin(Expr arg) {
		super(arg);
		updateLabel();
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
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		Expr sl = arg.subs(from, to);
		if(sl == arg)
			return this;
		return new Sin(sl);
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
		if(arg.getType() == TYPE.MATRIX || arg.getType() == TYPE.VECTOR) {
			il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "sin",
					new ObjectType("Jama.Matrix"), 
					new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKESTATIC));
		} else {
			il.append(factory.createInvoke("java.lang.Math", "sin",
					Type.DOUBLE, 
					new Type[] { Type.DOUBLE },
					Constants.INVOKESTATIC));
		}
		return startPos;
	}

	@Override
	public void updateLabel() {
		label = "sin(" + arg + ")";
		sortKey = label;
	}
}
