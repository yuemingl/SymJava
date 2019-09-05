package io.lambdacloud.symjava.symbolic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import io.lambdacloud.symjava.symbolic.Expr.TYPE;
import io.lambdacloud.symjava.symbolic.arity.UnaryOp;
import io.lambdacloud.symjava.symbolic.utils.Utils;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		updateLabel();
	}
	
	@Override
	public Expr diff(Expr expr) {
		Expr d = arg.diff(expr);
		if(d instanceof SymReal<?>) {
			if(d instanceof SymInteger) {
				return new SymInteger(-((SymInteger)d).getValue());
			}
			if(d instanceof SymLong) {
				return new SymLong(-((SymLong)d).getValue());
			}
			SymReal<?> dd = (SymReal<?>)d;
			double dv = dd.getValue().doubleValue();
			if(dv == 0.0)
				return new SymDouble(0.0);
			return new SymDouble(-dv);
		}
		return Negate.simplifiedIns(arg.diff(expr));
	}
	
	public static Expr simplifiedIns(Expr expr) {
		if(expr instanceof SymReal<?>) {
			Number n = ((SymReal<?>)expr).getValue();
			return new SymDouble(-n.doubleValue());
		} else if(expr instanceof Negate) {
			Negate n = (Negate)expr;
			return n.arg;
		}
		return new Negate(expr);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(arg.subs(from,to) == arg) 
			return this;
		return Negate.simplifiedIns(arg.subs(from, to));
	}

	@Override
	public Expr simplify() {
		if(this.isSimplified)
			return this;
		Expr nb = arg.simplify();
		nb.isSimplified = true;
		Expr rlt = new Negate(nb);
		rlt.isSimplified = true;
		return rlt;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Negate && arg.symEquals(((Negate)other).arg))
			return true;
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		arg.flattenAdd(list1);
		if(list1.size() == 1) { 
			outList.add(this);
		} else {
			for(Expr e : list1) {
				outList.add( new Negate(e) );
			}
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		List<Expr> tmp = new ArrayList<Expr>();
		arg.flattenMultiply(tmp);
		if(tmp.size() == 1)
			outList.add(this);
		else {
			int sign = Utils.getMultiplyGlobalSign(tmp);
			Utils.removeNegate(tmp);
			if(sign == 1)
				outList.add(new Negate(tmp.get(0)));
			else
				outList.add(tmp.get(0));
			for(int i=1; i<tmp.size(); i++) {
				outList.add(tmp.get(i));
			}
		}
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg.getType() == TYPE.MATRIX || arg.getType() == TYPE.VECTOR) {
			il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "negate",
					new ObjectType("Jama.Matrix"), 
					new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKESTATIC));
		} else {
			il.append(InstructionConstants.DNEG);
		}

		return startPos;
	}

	@Override
	public void updateLabel() {
		label = "-" + SymPrinting.addParenthsesIfNeeded(arg, this);
		sortKey = arg.getSortKey();
	}
}
