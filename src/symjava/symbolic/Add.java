package symjava.symbolic;

import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.DADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.FADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.IADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.LADD;

import java.util.List;
import java.util.Map;

import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Add extends BinaryOp {
	public Add(Expr l, Expr r) {
		super(l, r);
		updateLabel();
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getValue()+ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getValue()+ir.getValue()).setSimplifyOps(simOps).setAsSimplified();
			}
			Number t1 = (Number)((SymReal<?>)l).getValue();
			Number t2 = (Number)((SymReal<?>)r).getValue();
			return new SymDouble(t1.doubleValue() + t2.doubleValue()).setSimplifyOps(simOps).setAsSimplified();
		} else if(Symbol.C0.symEquals(l))
			return r.clone().setSimplifyOps(simOps).setAsSimplified();
		else if(Symbol.C0.symEquals(r)) {
			return l.clone().setSimplifyOps(simOps).setAsSimplified();
		} else if(l instanceof Negate && r instanceof Negate) {
			Negate nl = (Negate)l;
			Negate nr = (Negate)r;
			return new Negate(Add.shallowSimplifiedIns(nl.arg, nr.arg)).setSimplifyOps(simOps).setAsSimplified();
		} else if(l instanceof Negate) {
			Negate nl = (Negate)l;
			return Subtract.shallowSimplifiedIns(r, nl.arg); //Do not increase simplifyOps
		} else if(r instanceof Negate) {
			Negate nr = (Negate)r;
			return Subtract.shallowSimplifiedIns(l, nr.arg); //Do not increase simplifyOps
		} else if(l instanceof Multiply && r instanceof Multiply) {
			Multiply ml = (Multiply)l;
			Multiply mr = (Multiply)r;
			if(ml.isCoeffMulSymbol() && mr.isCoeffMulSymbol()) {
				if(Utils.symCompare(ml.getSymbolTerm(), mr.getSymbolTerm())) {
					Expr coeff = ml.getCoeffTerm().add(mr.getCoeffTerm());
					return coeff.multiply(ml.getSymbolTerm());
				}
			} else if(ml.isCoeffMulSymbol()) {
				if(Utils.symCompare(ml.getSymbolTerm(), r)) {
					Expr coeff = ml.getCoeffTerm().add(Symbol.C1);
					return coeff.multiply(r); 
				}
			} else if(mr.isCoeffMulSymbol()) {
				if(Utils.symCompare(mr.getSymbolTerm(), l)) {
					Expr coeff = mr.getCoeffTerm().add(Symbol.C1);
					return coeff.multiply(l);
				}
			}
		} else if(l instanceof Multiply) {
			Multiply ml = (Multiply)l;
			if(ml.isCoeffMulSymbol()) {
				if(Utils.symCompare(ml.getSymbolTerm(), r)) {
					Expr coeff = ml.getCoeffTerm().add(Symbol.C1);
					return coeff.multiply(r); 
				}
			}
		} else if(r instanceof Multiply) {
			Multiply mr = (Multiply)r;
			if(mr.isCoeffMulSymbol()) {
				if(Utils.symCompare(mr.getSymbolTerm(), l)) {
				Expr coeff = mr.getCoeffTerm().add(Symbol.C1);
				return coeff.multiply(l);
				}
			}
		}
		if(Utils.symCompare(l, r)) {
			return Symbol.C2.multiply(l).incSimplifyOps(1);
		}
		return new Add(l, r).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		//return shallowSimplifiedIns(l,r);
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l,r));
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		Expr sl = arg1.subs(from, to);
		Expr sr = arg2.subs(from, to);
		if(sl == arg1 && sr == arg2)
			return this;
		return new Add(sl, sr);
	}

	@Override
	public Expr diff(Expr expr) {
		return arg1.diff(expr).add(arg2.diff(expr));
	}

	@Override
	public Expr simplify() {
		if(this.isSimplified)
			return this;
		return simplifiedIns(arg1, arg2);
	}

	public void flattenAdd(List<Expr> outList) {
		if(device != null) {
			outList.add(this);
		} else {
			arg1.flattenAdd(outList);
			arg2.flattenAdd(outList);
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
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
			il.append(factory.createInvoke("Jama.Matrix", "plus",
					new ObjectType("Jama.Matrix"), new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKEVIRTUAL));
			return startPos;
		} else if(arg1.getType() == TYPE.VECTOR && arg2.getType() == TYPE.VECTOR) {
			arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(factory.createInvoke("Jama.Matrix", "plus",
					new ObjectType("Jama.Matrix"), new Type[] { new ObjectType("Jama.Matrix") },
					Constants.INVOKEVIRTUAL));
			return startPos;
		}
		
		TYPE ty = Utils.getConvertedType(arg1.getType(), arg2.getType());
		BytecodeUtils.typeCast(il, arg1.getType(), ty);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		BytecodeUtils.typeCast(il, arg2.getType(), ty);
		if(ty == TYPE.DOUBLE)
			il.append(DADD);
		else if(ty == TYPE.INT)
			il.append(IADD);
		else if(ty == TYPE.LONG)
			il.append(LADD);
		else if(ty == TYPE.FLOAT)
			il.append(FADD);
		else
			il.append(IADD);
		return startPos;
	}

	@Override
	public void updateLabel() {
		label = arg1 + " + " + arg2;
		sortKey = arg1.getSortKey()+arg2.getSortKey();
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return arg1.getTypeInfo();
	}	
}
