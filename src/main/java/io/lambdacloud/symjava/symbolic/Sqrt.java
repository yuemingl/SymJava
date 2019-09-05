package io.lambdacloud.symjava.symbolic;

import java.util.Map;

import io.lambdacloud.symjava.symbolic.arity.BinaryOp;
import io.lambdacloud.symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Sqrt extends BinaryOp {
	public Sqrt(Expr expr) {
		super(expr, Expr.valueOf(2));
		updateLabel();
	}
	
	public Sqrt(Expr expr, Expr root) {
		super(expr, root);
		updateLabel();
	}

	@Override
	public Expr diff(Expr expr) {
		return Pow.simplifiedIns(arg1, arg2.divideRev(1.0)).diff(expr);
	}

	public static Expr simplifiedIns(Expr expr, Expr root) {
		if(expr instanceof SymReal<?> && root instanceof SymReal<?>) {
			return new SymDouble(Math.pow(
					((SymReal<?>)expr).getDoubleValue(), 
					1.0/((SymReal<?>)root).getDoubleValue())
					);
		} else if(expr instanceof SymReal<?>) {
			SymReal<?> realBase = (SymReal<?>)expr;
			if(realBase.isZero())
				return Symbol.C0;
			else if(realBase.isOne())
				return Symbol.C1;
		}else if(root instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)root;
			if(realExp.isZero())
				return Symbol.C1;
			else if(realExp.isOne())
				return expr;
			else if(realExp.isNegativeOne())
				return Reciprocal.simplifiedIns(expr);
		}
		return new Sqrt(expr, root);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sqrt) {
			Sqrt o = (Sqrt)other;
			if(Utils.symCompare(arg1,  o.arg1) && Utils.symCompare(arg2, o.arg2))
				return true;
		}
		return false;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Sqrt(arg1.subs(from, to), arg2.subs(from, to));
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(InstructionConstants.DCONST_1);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg2 instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)arg2;
			if(realExp.isInteger()) {
				il.append(InstructionConstants.I2D);
			}
		}
		il.append(InstructionConstants.DDIV);
		il.append(factory.createInvoke("java.lang.Math", "pow",
				Type.DOUBLE, 
				new Type[] { Type.DOUBLE, Type.DOUBLE },
		Constants.INVOKESTATIC));
		return startPos;
	}

	@Override
	public void updateLabel() {
		if(arg2 instanceof SymReal<?>) {
			SymReal<?> t = (SymReal<?>)arg2;
			if(t.isInteger() && t.getIntValue() == 2) {
				label = "sqrt(" + arg1 + ")";
				sortKey = arg1.getSortKey()+"sqrt[2]"+arg2;
				return;
			}
		}
		label = "sqrtn(" + arg1 + "," + arg2 + ")";
		sortKey = arg1.getSortKey()+"sqrt"+arg2.getSortKey();
	}
}
