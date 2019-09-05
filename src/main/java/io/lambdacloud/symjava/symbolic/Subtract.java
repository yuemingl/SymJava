package io.lambdacloud.symjava.symbolic;

import java.util.List;
import java.util.Map;

import io.lambdacloud.symjava.symbolic.Expr.TYPE;
import io.lambdacloud.symjava.symbolic.arity.BinaryOp;
import io.lambdacloud.symjava.symbolic.utils.BytecodeUtils;
import io.lambdacloud.symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		super(l, r);
		updateLabel();

	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Subtract(arg1.subs(from, to), arg2.subs(from, to));
	}

	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(Utils.symCompare(l, r)) {
			return new SymInteger(0).setSimplifyOps(simOps).setAsSimplified();
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getValue()-ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getValue()-ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			}
			Number t1 = (Number)((SymReal<?>)l).getValue();
			Number t2 = (Number)((SymReal<?>)r).getValue();
			return new SymDouble(t1.doubleValue() - t2.doubleValue()).setSimplifyOps(simOps).setAsSimplified();
		} else if(Symbol.C0.symEquals(r))
			return l.clone().setSimplifyOps(simOps).setAsSimplified();
		else if(Symbol.C0.symEquals(l))
			return new Negate(r).setSimplifyOps(simOps).setAsSimplified();
		return new Subtract(l, r).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		//return shallowSimplifiedIns(l,r);
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l,r));
	}
	
	@Override
	public Expr diff(Expr expr) {
		return arg1.diff(expr).subtract(arg2.diff(expr));
	}

	@Override
	public Expr simplify() {
		if(!this.isSimplified) {
			return simplifiedIns(arg1, arg2);
		}
		return this;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		arg1.flattenAdd(outList);
		new Negate(arg2).flattenAdd(outList);
	}
	
	public boolean symEquals(Expr other) {
		//return Utils.flattenSortAndCompare(this, other);
		return Utils.flattenSortAndCompare(this.simplify(), other.simplify());
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg1.getType() == TYPE.MATRIX && arg2.getType() == TYPE.MATRIX) {
			arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(factory.createInvoke("Jama.Matrix", "minus",
					new ObjectType("Jama.Matrix"), new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKEVIRTUAL));
			return startPos;
		} else if(arg1.getType() == TYPE.VECTOR && arg2.getType() == TYPE.VECTOR) {
			arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(factory.createInvoke("Jama.Matrix", "minus",
					new ObjectType("Jama.Matrix"), new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKEVIRTUAL));
			return startPos;
		}
		
		TYPE ty = Utils.getConvertedType(arg1.getType(), arg2.getType());
		BytecodeUtils.typeCast(il, arg1.getType(), ty);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		BytecodeUtils.typeCast(il, arg2.getType(), ty);
		if(ty == TYPE.DOUBLE)
			il.append(InstructionConstants.DSUB);
		else if(ty == TYPE.INT)
			il.append(InstructionConstants.ISUB);
		else if(ty == TYPE.LONG)
			il.append(InstructionConstants.LSUB);
		else if(ty == TYPE.FLOAT)
			il.append(InstructionConstants.FSUB);
		else
			il.append(InstructionConstants.ISUB);
		return startPos;
	}

	@Override
	public void updateLabel() {
		if(arg2 instanceof Add || arg2 instanceof Subtract)
			label = arg1 + " - (" + arg2 + ")";
		else
			label = arg1 + " - " + arg2;

		sortKey = arg1.getSortKey()+arg2.getSortKey();
	}
}
