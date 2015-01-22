package symjava.symbolic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Sum extends Expr {
	public Expr summand;
	public Symbol indexSym;
	public int start;
	public int end;
	HashMap<Integer, Expr> cache = new HashMap<Integer, Expr>();
	
	public Sum(Expr summandTemplate, Symbol indexSym, int start, int end) {
 		this.summand = summandTemplate;
 		this.indexSym = indexSym;
 		
		label = "\\Sigma_{"+indexSym+"="+start+"}^" + end + "{" + SymPrinting.addParenthsesIfNeeded(summandTemplate, new Add(Symbol.x, Symbol.y)) + "}";
		this.start = start;
		this.end = end;
		sortKey = label;
	}
	
	public Expr getSummand(int index) {
		Expr s = cache.get(index);
		if(s == null) {
			s = summand.subs(indexSym, index);
			cache.put(index, s);
		}
		return s;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		//if(from == indexVar) {
		//	return new Summation(summand, to, start, end);
		//}
		return new Sum(summand.subs(from, to).simplify(), indexSym, start, end);
	}

	@Override
	public Expr diff(Expr expr) {
		if(expr instanceof Symbol) {
			boolean isContain = Utils.containSymbol(this, (Symbol)expr);
			if(!isContain)
				return Symbol.C0;
			Symbol x = (Symbol)expr;
			if(x.containsSubIndex()) {
				Expr smd = this.getSummand(x.getSubIndex());
				return smd.diff(expr);
			}
		}
		return new Sum(summand.diff(expr), indexSym, start, end);
	}

	@Override
	public Expr simplify() {
		List<Expr> ss = Utils.extractSymbols(this.summand);
		if(!Utils.containSymbol(this.summand, indexSym))
			return summand.multiply(end-start+1);
		if(ss.size() == 1) { //This should be indexSym
			List<Expr> list = new ArrayList<Expr>();
			for(int i=start; i<=end; i++) {
				list.add(summand.subs(indexSym, i));
			}
			return Utils.addListToExpr(list).simplify();
		}
		return new Sum(summand.simplify(), indexSym, start, end);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sum) {
			Sum o = (Sum)other;
			if(summand.symEquals(o.summand) && indexSym.symEquals(o.indexSym) && 
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
