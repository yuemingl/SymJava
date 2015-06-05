package symjava.symbolic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import symjava.symbolic.utils.AddList;
import symjava.symbolic.utils.Utils;

/**
 * a+b+c+d+...
 */
public class Sum extends Expr {
	public Expr summandTemplate;
	public Expr indexExpr;
	public int start;
	public int end;
	HashMap<Integer, Expr> cache = new HashMap<Integer, Expr>();
	
	public Sum(Expr summandTemplate, Expr indexExpr, int start, int end) {
 		this.summandTemplate = summandTemplate;
 		this.indexExpr = indexExpr;
 		
		label = "\\Sigma_{"+indexExpr+"="+start+"}^" + end + "{" + SymPrinting.addParenthsesIfNeeded(summandTemplate, new Add(Symbol.x, Symbol.y)) + "}";
		this.start = start;
		this.end = end;
		sortKey = label;
	}
	
	public static Sum apply(Expr summandTemplate, Expr indexExpr, int start, int end) {
		return new Sum(summandTemplate, indexExpr, start, end);
	}
	
	public Expr getSummand(int index) {
		Expr s = cache.get(index);
		if(s == null) {
			if(indexExpr instanceof Symbol) {
				s = summandTemplate.subs(indexExpr, index).simplify();
				cache.put(index, s);
			} else {
				List<Expr> exprs = Utils.extractSymbols(indexExpr);
				if(exprs.size() > 1) {
					throw new RuntimeException("Please call getSummand(Expr indexSymbol, int index).");
				} else if(exprs.size() == 1){
					Expr indexSymbol = exprs.get(0);
					s = summandTemplate.subs(indexExpr, indexExpr.subs(indexSymbol, index).simplify());
					cache.put(index, s);
				} else {
					return this;
				}
			}
		}
		return s;
	}
	
	public Expr getSummand(Symbol indexSymbol, int index) {
		Expr s = cache.get(index);
		if(s == null) {
			s = summandTemplate.subs(indexExpr, indexExpr.subs(indexSymbol, index).simplify());
			cache.put(index, s);
		}
		return s;
	}
	

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Sum(summandTemplate.subs(from, to).simplify(),
				indexExpr.subs(from, to), start, end);
	}

	@Override
	public Expr diff(Expr expr) {
		AddList addList = new AddList();
		for(int i=start; i<=end; i++) {
			Expr summand = this.getSummand(i).diff(expr);
			if(!Utils.symCompare(Symbol.C0, summand))
				addList.add(summand);
		}
		if(addList.size() == 0)
			addList.add(Symbol.C0);
		return addList.toExpr().simplify();
	}

	@Override
	public Expr simplify() {
		List<Expr> ss = Utils.extractSymbols(this.summandTemplate);
		if(indexExpr instanceof Symbol) {
			if(!Utils.containSymbol(this.summandTemplate, (Symbol)indexExpr))
				return summandTemplate.multiply(end-start+1);
		}
		if(ss.size() == 1) { //This should be indexSym
			List<Expr> list = new ArrayList<Expr>();
			for(int i=start; i<=end; i++) {
				list.add(summandTemplate.subs(indexExpr, i));
			}
			return Utils.addListToExpr(list).simplify();
		}
		return new Sum(summandTemplate.simplify(), indexExpr, start, end);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sum) {
			Sum o = (Sum)other;
			if(summandTemplate.symEquals(o.summandTemplate) && indexExpr.symEquals(o.indexExpr) && 
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

	@Override
	public TYPE getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
