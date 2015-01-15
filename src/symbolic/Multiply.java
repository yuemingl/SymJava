package symbolic;

import java.util.ArrayList;
import java.util.List;

import symbolic.utils.Utils;

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
		name =  SymPrinting.addParenthsesIfNeeded(left, this) 
				+ " * " + 
				SymPrinting.addParenthsesIfNeeded(right, this);
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();
		if(Symbol.C1.symEquals(l))
			return r.incSimplifyOps(1);
		else if(Symbol.C1.symEquals(r))
			return l.incSimplifyOps(1);
		else if(Symbol.C0.symEquals(l) || Symbol.C0.symEquals(r))
			return Symbol.C0.incSimplifyOps(1);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() * t2.doubleValue()).setSimplifyOps(
					l.getSimplifyOps() + r.getSimplifyOps() + 1
					);
		} else if(Utils.symCompare(l, r)) {
				return new Power(l, 2).setSimplifyOps(l.getSimplifyOps() + r.getSimplifyOps() + 1);
		} else if(Symbol.Cm1.symEquals(l)) {
			return new Negate(r).incSimplifyOps(1);
		} else if(Symbol.Cm1.symEquals(r)) {
			return new Negate(l).incSimplifyOps(1);
		} else if(l instanceof Reciprocal && r instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			Reciprocal rr = (Reciprocal)r;
			return new Reciprocal( simplifiedIns(rl.base, rr.base) ).incSimplifyOps(1);
		} else if(l instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			return Divide.simplifiedIns(r, rl.base).incSimplifyOps(1);
		} else if(r instanceof Reciprocal) {
			Reciprocal rr = (Reciprocal)r;
			return Divide.simplifiedIns(l, rr.base).incSimplifyOps(1);
		} else {
			//dead loop?
			List<Expr> ll = Utils.flattenAddAndSort(l);
			List<Expr> lr = Utils.flattenAddAndSort(r);
			if(ll.size() == 1 && lr.size() == 1) {
				return new Multiply(l, r);
			}
			List<Expr> addList = new ArrayList<Expr>();
			for(Expr e1 : ll) {
				for(Expr e2 : lr) {
					addList.add(simplifiedIns(e1, e2));
				}
			}
			List<Expr> simList = Utils.simplifyAddList(Utils.addListToExpr(addList));
			List<Expr> simList2 = new ArrayList<Expr>();
			for(Expr e : simList) {
				simList2.add(Utils.multiplyListToExpr(Utils.simplifyMultiplyList(e)));
			}
			return Utils.addListToExpr(simList2);
		}
		
//		else if((l instanceof SymReal<?>) && r instanceof Multiply) {
//			Multiply rr = (Multiply)r;
//			if(rr.left instanceof SymReal<?>) {
//				Number t1 = (Number)((SymReal<?>)l).getVal();
//				Number t2 = (Number)((SymReal<?>)rr.left).getVal();
//				double coef = t1.doubleValue()*t2.doubleValue();
//				if(coef == 1.0) 
//					return rr.right;
//				return new Multiply(new SymDouble(coef), rr.right).setSimplifyOps(
//						l.getSimplifyOps() + r.getSimplifyOps() + 1
//						);
//			}
//		}
		
//		else if(l instanceof Multiply && r instanceof Multiply) {
//			Multiply a1 = (Multiply)l;
//			Multiply a2 = (Multiply)r;
//			int maxSimplifyOps = -1;
//			Expr simplest = null;
//			List<Tuple4<Expr>> coms = Utils.C_4_2(a1.left, a1.right, a2.left, a2.right);
//			for(Utils.Tuple4<Expr> com : coms) {
//				Expr tmp = new Multiply( simplifiedIns(com.o1, com.o2), simplifiedIns(com.o3, com.o4) );
//				//System.out.println(tmp+"->"+tmp.getSimplifyOps());
//				if(tmp.getSimplifyOps() > maxSimplifyOps) {
//					maxSimplifyOps = tmp.getSimplifyOps();
//					simplest = tmp;
//				}
//			}
//			return simplest;
//		} else if(l instanceof Add && r instanceof Multiply) {
//			//TODO
//		} else if(l instanceof Multiply && r instanceof Add) {
//			//TODO
//		}
		
//		return new Multiply(l, r);
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
		return simplifiedIns(left, right);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
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
	public void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		right.flattenMultiply(outList);
	}
}
