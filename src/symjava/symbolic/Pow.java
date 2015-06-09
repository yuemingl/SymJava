package symjava.symbolic;

import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.POP2;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Pow extends BinaryOp {
	
	public Pow(Expr base, Expr exponent) {
		super(base, exponent);
		String displayExp = String.format("%s", this.arg2);
		if(exponent instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)exponent;
			if(realExp.isInteger()) {
				displayExp = String.format("%d", realExp.getIntValue());
			}
			if(realExp.isNegative())
				displayExp = "{"+displayExp+"}";
		}
		if(base instanceof Symbol) {
			label = base + "^" + displayExp + "";
			label = "pow(" + base + ","+displayExp+")";
		} else {
			label = "("+base + ")^" + displayExp;
		}
		//TODO? x^3 + x^2 + x + 1
		sortKey = base.getSortKey()+"power"+String.valueOf(displayExp);
	}

	public static Expr simplifiedIns(Expr base, Expr exponent) {
		if(base instanceof SymReal<?> && exponent instanceof SymReal<?>) {
			return new SymDouble(Math.pow(
					((SymReal<?>)base).getDoubleValue(), 
					((SymReal<?>)exponent).getDoubleValue())
					);
		} else if(base instanceof SymReal<?>) {
			SymReal<?> realBase = (SymReal<?>)base;
			if(realBase.isZero())
				return Symbol.C0;
			else if(realBase.isOne())
				return Symbol.C1;
		}else if(exponent instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)exponent;
			if(realExp.isZero())
				return Symbol.C1;
			else if(realExp.isOne())
				return base;
			else if(realExp.isNegativeOne())
				return Reciprocal.simplifiedIns(base);
		}
		return new Pow(base, exponent);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return Pow.simplifiedIns(arg1.subs(from, to), arg2.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		if(arg2 instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)arg2;
			return realExp.multiply(Pow.simplifiedIns(arg1, arg2.subtract(1))).multiply(arg1.diff(expr));
		} else {
			Expr x = expr;
			Expr b = arg1;
			Expr e = arg2;
			Expr y = this;
			Expr log_b = Log.simplifiedIns(b);
			Expr term1 = e.diff(x).multiply(log_b);
			Expr term2 = e.multiply(b.diff(x)).divide(b);
			return y.multiply(term1.add(term2));
		}
	}

	@Override
	public Expr simplify() {
		return Pow.simplifiedIns(arg1.simplify(), arg2.simplify());
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Pow) {
			Pow o = (Pow)other;
			if(Utils.symCompare(arg1,  o.arg1) && Utils.symCompare(arg2, o.arg2))
				return true;
		}
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		//TODO
		//Do we need to do this: (a+b)^2 => a^2 +2ab+b^2
		outList.add(this);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(arg2 instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)arg2;
			if(realExp.isPositive()) {
				double exp = realExp.getDoubleValue();
				for(int i=0; i<(int)exp; i++)
					outList.add(arg1);
				double remain = exp - Math.floor(exp);
				if(remain > 0.0) {
					outList.add(simplifiedIns(arg1, Expr.valueOf(remain)));
				}
				return;
			}
		}
		outList.add(this);
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg2 instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)arg2;
			if(realExp.isInteger()) {
				il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "powi",
						Type.DOUBLE, new Type[] { Type.DOUBLE, Type.INT }, Constants.INVOKESTATIC));
				return startPos;
			}
		}
		il.append(factory.createInvoke("java.lang.Math", "pow",
				Type.DOUBLE, new Type[] { Type.DOUBLE, Type.DOUBLE }, Constants.INVOKESTATIC));
		return startPos;
	}		
}
