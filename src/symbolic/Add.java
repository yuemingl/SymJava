package symbolic;

import java.util.List;

public class Add extends BinaryOp {
	public Add(Expr l, Expr r) {
		super(l, r);
		name = l + " + " + r;
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();
		if(Symbol.C0.symEquals(l))
			return r.simplify().incSimplifyOps(1);
		else if(Symbol.C0.symEquals(r))
			return l.simplify().incSimplifyOps(1);
		else if(Utils.symCompare(l, r)) {
			return Symbol.C2.multiply(l).setSimplifyOps(l.getSimplifyOps()+r.getSimplifyOps() + 1);
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() + t2.doubleValue()).
					setSimplifyOps(l.getSimplifyOps() + r.getSimplifyOps() + 1);
		} else {
			List<Expr> simList = Utils.simplifyAddList(l, r);
			if(simList.size() == 1)
				return simList.get(0);
			else {
				Expr rlt = simList.get(0);
				for(int i=1; i<simList.size(); i++) {
					rlt = new Add(rlt, simList.get(i));
				}
				return rlt;
			}
		}
		
//		else if(l instanceof Add && r instanceof Add) {
//			Add a1 = (Add)l;
//			Add a2 = (Add)r;
//			int maxSimplifyOps = -1;
//			Expr simplest = null;
//			//List<Tuple4<Expr>> coms = Utils.C_4_2(a1.left, a1.right, a2.left, a2.right);
//			List<Tuple4<Expr>> coms = Utils.C_4_2(
//					a1.left.simplify(), 
//					a1.right.simplify(), 
//					a2.left.simplify(), 
//					a2.right.simplify()
//					);
//			for(Utils.Tuple4<Expr> com : coms) {
//				//if(a1.left == com.o1 && a1.right == com.o2 && a2.left == com.o3 && a2.right == com.o4)
//				//	continue;
//				Expr tmp = new Add( simplifiedIns(com.o1, com.o2), simplifiedIns(com.o3, com.o4) );
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
//		} else if(l instanceof Add) {
//			Add a = (Add)l;
//			Expr tmp1 = new Add( a.left.simplify(), simplifiedIns(a.right, r) );
//			Expr tmp2 = new Add( simplifiedIns(a.left, r), a.right.simplify() );
//			if(tmp1.getSimplifyOps() >= tmp2.getSimplifyOps())
//				return tmp1;
//			return tmp2;
//		} else if(r instanceof Add) {
//			Add a = (Add)r;
//			Expr tmp1 = new Add( simplifiedIns(l, a.left), a.right.simplify() );
//			Expr tmp2 = new Add( a.left.simplify(), simplifiedIns(l, a.right));
//			if(tmp1.getSimplifyOps() >= tmp2.getSimplifyOps())
//				return tmp1;
//			return tmp2;
//		}
//		return new Add(l, r);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Add(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr) + right.diff(expr);
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left, right);
	}

	public void flattenAdd(List<Expr> outList) {
		left.flattenAdd(outList);
		right.flattenAdd(outList);
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
		
}
