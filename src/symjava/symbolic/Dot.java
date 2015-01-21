package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.matrix.SymVector;
import symjava.symbolic.utils.Utils;

public class Dot extends Expr {
	SymVector left;
	SymVector right;
	Expr expr = null;
	public Dot(SymVector l, SymVector r) {
		if(l.dim() != r.dim())
			throw new IllegalArgumentException("The size of the two vector must be the same!");
		left = l;
		right = r;
		if(left instanceof Grad && right instanceof Grad) {
			label = left.getLabel() + " \\cdot " + right.getLabel();
			sortKey = label;
			return;
		}
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<left.dim(); i++) {
			list.add(left.get(i).multiply(right.get(i)));
		}
		expr = Utils.addListToExpr(list).simplify();
		label = expr.toString();
		sortKey = label;
	}
	
	public static Expr apply(SymVector l, SymVector r) {
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<l.dim(); i++) {
			list.add(l.get(i).multiply(r.get(i)));
		}
		Expr ret = Utils.addListToExpr(list).simplify();
		if(ret instanceof SymReal<?>)
			return ret;
		Dot dot = new Dot(l, r);
		if(dot.expr != null)
			return dot.expr;
		return dot;
	}
	
	@Override
	public Expr diff(Expr expr) {
		if(this.expr == null) {
			Grad lg = (Grad)left;
			Grad rg = (Grad)right;
			if(lg.isAbstract() && rg.isAbstract()) {
				Expr d1 = Dot.apply(new Grad(lg.getFunc().diff(expr), lg.getFunc().args), rg);
				Expr d2 = Dot.apply(lg, new Grad(rg.getFunc().diff(expr), rg.getFunc().args));
				return Add.simplifiedIns(d1, d2);
			}
		}
		if(left instanceof Grad && right instanceof Grad) {
			Grad lg = (Grad)left;
			Grad rg = (Grad)right;
			Expr d1 = Dot.apply(lg.diff(expr), rg);
			Expr d2 = Dot.apply(lg, rg.diff(expr));
			return Add.simplifiedIns(d1, d2);
		}
		return this.expr.diff(expr);
	}

	@Override
	public Expr fdiff(Expr f, Expr df) {
		if(expr == null) {
			Grad lg = (Grad)left;
			Grad rg = (Grad)right;
			if(lg.isAbstract() && rg.isAbstract()) {
				Expr d1 = Dot.apply(new Grad(lg.getFunc().fdiff(f, df)), rg);
				Expr d2 = Dot.apply(lg, new Grad(rg.getFunc().fdiff(f, df)));				
				return Add.shallowSimplifiedIns(d1, d2);
			}
		}
		return expr.fdiff(f, df);
	}
	
	@Override
	public Expr simplify() {
		if(expr == null)
			return this;
		return expr.simplify();
	}

	@Override
	public boolean symEquals(Expr other) {
		if(expr == null) {
			if(other instanceof Dot) {
				//TODO
			}
			return false;
		}
		return Utils.symCompare(expr, other);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		if(expr == null)
			outList.add(this);
		else
			expr.flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(expr == null)
			outList.add(this);
		else
			expr.flattenMultiply(outList);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(expr == null)
			return new Dot(left.subs(from, to), right.subs(from, to));
		else
			return expr.subs(from, to);
	}
	
	public Expr getExpr() {
		if(this.expr != null)
			return this.expr;
		else {
			List<Expr> list = new ArrayList<Expr>();
			for(int i=0; i<left.dim(); i++) {
				list.add(left.get(i).multiply(right.get(i)));
			}
			return Utils.addListToExpr(list).simplify();
		}
	}
}
