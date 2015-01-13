package symbolic;

import java.util.ArrayList;
import java.util.List;

import symbolic.Utils.Tuple4;

public class Multiply extends BinaryOp {
	public Multiply(Expr l, Expr r) {
		super(l, r);
		if((!(l instanceof SymReal) && !(l instanceof SymInteger)) &&
				(r instanceof SymReal || r instanceof SymInteger)) {
			left = r;
			right = l;
		} else {
			left = l;
			right = r;
		}
		name = left + " * " + right;
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();		
		if(l.symEquals(Symbol.C1))
			return r;
		else if(r.symEquals(Symbol.C1))
			return l;
		else if(l.symEquals(Symbol.C0) || r.symEquals(Symbol.C0))
			return Symbol.C0;
		else if(l.symEquals(r)) {
			Power p = new Power(l, 2);
			p.setSimplifyOps(Math.max(l.getSimplifyOps(), r.getSimplifyOps()) + 1);
			return p;
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			Expr rlt = new SymDouble(t1.doubleValue() * t2.doubleValue());
			rlt.setSimplifyOps(l.getSimplifyOps() + r.getSimplifyOps() + 1);
			return rlt;
		} else if(l.symEquals(Symbol.Cm1)) {
			return new Negate(r);
		} else if(r.symEquals(Symbol.Cm1)) {
			return new Negate(l);
		} else if((l instanceof SymReal<?>) && r instanceof Multiply) {
			Multiply rr = (Multiply)r;
			if(rr.left instanceof SymReal<?>) {
				Number t1 = (Number)((SymReal<?>)l).getVal();
				Number t2 = (Number)((SymReal<?>)rr.left).getVal();
				double coef = t1.doubleValue()*t2.doubleValue();
				if(coef == 1.0) 
					return rr.right;
				return new Multiply(new SymDouble(coef), rr.right);
			}
		} else if(l instanceof Multiply && r instanceof Multiply) {
			Multiply a1 = (Multiply)l;
			Multiply a2 = (Multiply)r;
			int maxSimplifyOps = -1;
			Expr simplest = null;
			List<Tuple4<Expr>> coms = Utils.C_4_2(a1.left, a1.right, a2.left, a2.right);
			for(Utils.Tuple4<Expr> com : coms) {
				Expr tmp = new Multiply( simplifiedIns(com.o1, com.o2), simplifiedIns(com.o3, com.o4) );
				//System.out.println(tmp+"->"+tmp.getSimplifyOps());
				if(tmp.getSimplifyOps() > maxSimplifyOps) {
					maxSimplifyOps = tmp.getSimplifyOps();
					simplest = tmp;
				}
			}
			return simplest;
		} else if(l instanceof Add && r instanceof Multiply) {
			//TODO
		} else if(l instanceof Multiply && r instanceof Add) {
			//TODO
		}
		return new Multiply(l, r);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Multiply(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr).multiply(right).add(left.multiply(right.diff(expr)));
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left.simplify(), right.simplify());
	}

	@Override
	protected void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		List<Expr> list2 = new ArrayList<Expr>();
		left.flattenAdd(list1);
		right.flattenAdd(list2);
		if(list1.size()==1 && list2.size()==1)
			outList.add(this);
		else {
			for(Expr e1 : list1) {
				for(Expr e2 : list2) {
					outList.add( simplifiedIns(e1, e2) );
				}
			}
		}
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		right.flattenMultiply(outList);
	}
}
