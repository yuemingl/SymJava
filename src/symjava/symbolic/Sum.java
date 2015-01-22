package symjava.symbolic;

import java.util.HashMap;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Sum extends Expr {
	public Expr summand;
	public Symbol indexVar;
	public int start;
	public int end;
	HashMap<Integer, Expr> cache = new HashMap<Integer, Expr>();
	
	public Sum(Expr summandTemplate, Symbol indexVar, int start, int end) {
 		this.summand = summandTemplate;
 		this.indexVar = indexVar;
 		
		label = "\\Sigma_{"+indexVar+"="+start+"}^" + end + "{" + SymPrinting.addParenthsesIfNeeded2(summandTemplate, Add.simplifiedIns(Symbol.C0, Symbol.C0)) + "}";
		this.start = start;
		this.end = end;
		sortKey = label;
	}
	
	public Expr getSummand(int index) {
		Expr s = cache.get(index);
		if(s == null) {
			s = summand.subs(indexVar, index);
			cache.put(index, s);
		}
		return s;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		//if(from == indexVar) {
		//	return new Summation(summand, to, start, end);
		//}
		return new Sum(summand.subs(from, to).simplify(), indexVar, start, end);
	}

	@Override
	public Expr diff(Expr expr) {
		List<Expr> list = Utils.extractSymbols(this.summand);
		for(Expr e : list) {
			if(e instanceof Symbols) {
				Symbols s = (Symbols)e;
				if(expr.label.startsWith(s.namePrefix)) {
					return summand.diff(expr).subs(s, expr);
				}
			}
		}
		return new Sum(summand.diff(expr), indexVar, start, end);
	}

	@Override
	public Expr simplify() {
		return new Sum(summand.simplify(), indexVar, start, end);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sum) {
			Sum o = (Sum)other;
			if(summand.symEquals(o.summand) && indexVar.symEquals(o.indexVar) && 
					start == o.start && end == o.end)
				return true;
		}
		return false;
	}
	
	@Override
	public void flattenAdd(List<Expr> outList) {
		outList.add(this);
	}
	
	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}

}
