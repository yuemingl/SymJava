package symjava.symbolic;

import java.util.List;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Add extends BinaryOp {
	public Add(Expr l, Expr r) {
		super(l, r);
		label = l + " + " + r;
		sortKey = arg1.getSortKey()+arg2.getSortKey();
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getVal()+ir.getVal()).setSimplifyOps(simOps);
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getVal()+ir.getVal()).setSimplifyOps(simOps);
			}
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() + t2.doubleValue()).setSimplifyOps(simOps);
		} else if(Symbol.C0.symEquals(l))
			return r.clone().setSimplifyOps(simOps);
		else if(Symbol.C0.symEquals(r)) {
			return l.clone().setSimplifyOps(simOps);
		} else if(l instanceof Negate && r instanceof Negate) {
			Negate nl = (Negate)l;
			Negate nr = (Negate)r;
			return new Negate(Add.shallowSimplifiedIns(nl.arg, nr.arg)).setSimplifyOps(simOps);
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
		arg1.flattenAdd(outList);
		arg2.flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
	
	public boolean symEquals(Expr other) {
		//return Utils.flattenSortAndCompare(this, other);
		return Utils.flattenSortAndCompare(this.simplify(), other.simplify());
	}
}
