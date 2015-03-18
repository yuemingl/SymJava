package symjava.symbolic;

import java.util.List;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		super(l, r);
		if(arg2 instanceof Add || arg2 instanceof Subtract)
			label = arg1 + " - (" + arg2 + ")";
		else
			label = arg1 + " - " + arg2;

		sortKey = arg1.getSortKey()+arg2.getSortKey();
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
			return new SymInteger(0).setSimplifyOps(simOps);
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getValue()-ir.getValue()).setSimplifyOps(simOps);
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getValue()-ir.getValue()).setSimplifyOps(simOps);
			}
			Number t1 = (Number)((SymReal<?>)l).getValue();
			Number t2 = (Number)((SymReal<?>)r).getValue();
			return new SymDouble(t1.doubleValue() - t2.doubleValue()).setSimplifyOps(simOps);
		} else if(Symbol.C0.symEquals(r))
			return l.clone().setSimplifyOps(simOps);
		else if(Symbol.C0.symEquals(l))
			return new Negate(r).setSimplifyOps(simOps);
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
}
