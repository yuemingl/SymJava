package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class Multiply extends BinaryOp {
	public Multiply(Expr l, Expr r) {
		super(l, r);
		if((!(l instanceof SymReal) && !(l instanceof SymInteger)) &&
				(r instanceof SymReal || r instanceof SymInteger)) {
			arg1 = r;
			arg2 = l;
		} else {
			arg1 = l;
			arg2 = r;
		}
		label =  SymPrinting.addParenthsesIfNeeded(arg1, this) 
				+ "*" + 
				SymPrinting.addParenthsesIfNeeded(arg2, this);
		if(this.isCoeffMulSymbol()) {
			sortKey = this.getSymbolTerm().getSortKey();//+this.getCoeffTerm().getSortKey();
		} else {
			sortKey = arg1.getSortKey()+arg2.getSortKey();
		}
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getValue()*ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getValue()*ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			}
			Number t1 = (Number)((SymReal<?>)l).getValue();
			Number t2 = (Number)((SymReal<?>)r).getValue();
			return new SymDouble(t1.doubleValue() * t2.doubleValue()).setSimplifyOps(simOps).setAsSimplified();
		} else if(Symbol.C1.symEquals(l))
			return r.clone().setSimplifyOps(simOps).setAsSimplified();
		else if(Symbol.C1.symEquals(r))
			return l.clone().setSimplifyOps(simOps).setAsSimplified();
		else if(Symbol.C0.symEquals(l) || Symbol.C0.symEquals(r)) {
			//Here we need a new instance of 0 to hold the number of simplify operations
			return new SymInteger(0).setSimplifyOps(simOps).setAsSimplified();
		} else if(Symbol.Cm1.symEquals(l)) {
			return new Negate(r).setSimplifyOps(simOps).setAsSimplified();
		} else if(Symbol.Cm1.symEquals(r)) {
			return new Negate(l).setSimplifyOps(simOps).setAsSimplified();
		} else if(l instanceof Reciprocal && r instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			Reciprocal rr = (Reciprocal)r;
			Expr newBase = simplifiedIns(rl.arg, rr.arg);
			//SimplifyOps=? 
			return Reciprocal.simplifiedIns( newBase ).setSimplifyOps(simOps + newBase.getSimplifyOps() + 1).setAsSimplified();
		} else if(l instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			return Divide.shallowSimplifiedIns(r, rl.arg);
		} else if(r instanceof Reciprocal) {
			Reciprocal rr = (Reciprocal)r;
			return Divide.shallowSimplifiedIns(l, rr.arg);
		} else if(l instanceof Pow && r instanceof Pow) {
			Pow lp = (Pow)l;
			Pow rp = (Pow)r;
			if(Utils.symCompare(lp.arg1, rp.arg1)) {
				return Pow.simplifiedIns( lp.arg1, lp.arg2+rp.arg2).setSimplifyOps(simOps).setAsSimplified();
			} else if(lp.arg2 == rp.arg2) {
				return Pow.simplifiedIns( simplifiedIns(lp.arg1, rp.arg1), lp.arg2).setSimplifyOps(simOps).setAsSimplified();
			}
		} else if(l instanceof Pow) {
			Pow lp = (Pow)l;
			if(Utils.symCompare(lp.arg1, r)) {
				return new Pow(lp.arg1, lp.arg2 + 1).setSimplifyOps(simOps).setAsSimplified();
			}
		} else if(r instanceof Pow) {
			Pow rp = (Pow)r;
			if(Utils.symCompare(rp.arg1, l)) {
				return new Pow(rp.arg1, rp.arg2 + 1).setSimplifyOps(simOps).setAsSimplified();
			}
		}
		if(Utils.symCompare(l, r)) {
			return new Pow(l, Expr.valueOf(2)).setSimplifyOps(simOps).setAsSimplified();
		}
		return new Multiply(l, r).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		return shallowSimplifiedIns(l, r);
		//return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l, r));
	}
	
	boolean isCoeffMulSymbol() {
		if(arg1 instanceof SymReal<?> && !(arg2 instanceof SymReal<?>) )
			return true;
		if(arg2 instanceof SymReal<?> && !(arg1 instanceof SymReal<?>) )
			return true;
		return false;
	}
	public Expr getCoeffTerm() {
		if(arg1 instanceof SymReal<?> && !(arg2 instanceof SymReal<?>) )
			return arg1;
		if(arg2 instanceof SymReal<?> && !(arg1 instanceof SymReal<?>) )
			return arg2;
		return null;
	}
	public Expr getSymbolTerm() {
		if(arg1 instanceof SymReal<?> && !(arg2 instanceof SymReal<?>) )
			return arg2;
		if(arg2 instanceof SymReal<?> && !(arg1 instanceof SymReal<?>) )
			return arg1;
		return null;		
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Multiply(arg1.subs(from, to), arg2.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return arg1.diff(expr).multiply(arg2).add(arg1.multiply(arg2.diff(expr)));
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
		List<Expr> list1 = new ArrayList<Expr>();
		List<Expr> list2 = new ArrayList<Expr>();
		arg1.flattenAdd(list1);
		arg2.flattenAdd(list2);
		if(list1.size()==1 && list2.size()==1)
			outList.add(this);
		else {
			for(Expr e1 : list1) {
				for(Expr e2 : list2) {
					outList.add( shallowSimplifiedIns(e1, e2) );
				}
			}
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		arg1.flattenMultiply(outList);
		arg2.flattenMultiply(outList);
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
		TYPE ty = Utils.getConvertedType(arg1.getType(), arg2.getType());
		BytecodeUtils.typeCast(il, arg1.getType(), ty);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		BytecodeUtils.typeCast(il, arg2.getType(), ty);
		if(ty == TYPE.DOUBLE)
			il.append(InstructionConstants.DMUL);
		else if(ty == TYPE.INT)
			il.append(InstructionConstants.IMUL);
		else if(ty == TYPE.LONG)
			il.append(InstructionConstants.LMUL);
		else if(ty == TYPE.FLOAT)
			il.append(InstructionConstants.FMUL);
		else
			il.append(InstructionConstants.IMUL);
		return startPos;
	}	
}
