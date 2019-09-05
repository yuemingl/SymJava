package io.lambdacloud.symjava.symbolic;

import java.util.HashMap;
import java.util.List;

import io.lambdacloud.symjava.symbolic.utils.Utils;

/**
 * An object of Symbols can represents a list of symbols
 * For example:
 * Symbols ss = new Symbols("x"); //x_i, i=...,1,2,3...
 * Symbol x1 = ss.get(1); //x_1
 * Symbol x8 = ss.get(8); //x_8
 * 
 * This can be used to generate a list of symbols in summation 
 * or as a parameter list in an equation.
 * 
 * @author yuemingliu
 *
 */
public class Symbols extends Expr {
	Expr namePrefix;
	Expr indexExpr;
	HashMap<Integer, Symbol> cache = new HashMap<Integer, Symbol>();
	
	/**
	 * Create symbols like: 
	 *   x_i, i=...,1,2,3,...
	 * @param namePrefix
	 */
	public Symbols(String namePrefix) {
		this.namePrefix = new Symbol(namePrefix);
		this.indexExpr = new Symbol("i");
		updateLabel();
	}
	
	/**
	 * Create more complicated symbols like 
	 *   x_{i+j}, i=...,1,2,3,...
	 *   x_{i-1}, i=...,1,2,3,...
	 * 
	 * @param namePrefix
	 * @param indexExpr
	 */
	public Symbols(String namePrefix, Expr indexExpr) {
		this.namePrefix = new Symbol(namePrefix);
		this.indexExpr = indexExpr;
		updateLabel();
	}
	
	public Symbol get(int index) {
		Symbol s = cache.get(index);
		if(s == null) {
			if(indexExpr instanceof Symbols) {
				s = new Symbol(namePrefix+"_"+index);
				cache.put(index, s);
			} else {
				List<Expr> exprs = Utils.extractSymbols(indexExpr);
				if(exprs.size() > 1) {
					throw new RuntimeException("Please call get(Expr indexSymbol, int index).");
				} else if(exprs.size() == 1){
					Expr indexSymbol = exprs.get(0);
					Expr simpliedIndexExpr = indexExpr.subs(indexSymbol, index).simplify();
					if(simpliedIndexExpr instanceof Symbol || simpliedIndexExpr instanceof SymReal)
						s = new Symbol(namePrefix+"_"+simpliedIndexExpr.getLabel());
					else
						s = new Symbol(namePrefix+"_{"+simpliedIndexExpr.getLabel()+"}");
					cache.put(index, s);
				} else {
					throw new RuntimeException("No indexes found!");
				}
			}
		}
		return s;
	}
	
	public Symbol get(Symbol indexSymbol, int index) {
		Symbol s = cache.get(index);
		if(s == null) {
			Expr simpliedIndexExpr = indexExpr.subs(indexSymbol, index).simplify();
			if(simpliedIndexExpr instanceof Symbol || simpliedIndexExpr instanceof SymReal)
				s = new Symbol(namePrefix.getLabel() + "_"+simpliedIndexExpr.getLabel());
			else
				s = new Symbol(namePrefix.getLabel() + "_{"+simpliedIndexExpr.getLabel()+"}");
			cache.put(index, s);
		}
		return s;
	}
	
	/**
	 * Return an array of symbols: [startIdx, endIdx]
	 * which include startIdx and endIdx
	 * @param startIdx
	 * @param endIdx
	 * @return
	 */
	public Expr[] get(int startIdx, int endIdx) {
		Expr[] rlt = new Expr[endIdx-startIdx+1];
		for(int i=startIdx; i<=endIdx; i++)
			rlt[i-startIdx] = get(i);
		return rlt;
	}

	/**
	 * The substitution of symbols will return a symbol
	 * which behaves different from other class in SymJava
	 */
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		} else if(Utils.symCompare(indexExpr,from)) {
			if(to instanceof SymInteger) {
				SymInteger index = (SymInteger)to;
				Symbol s = this.get(index.getValue());
				//case: symbols.subs(i,3): x_i => x_3
				return s; 
			}
			//case: symbols.subs(i,j): x_i => x_j
			return new Symbol(namePrefix.getLabel() + "_{" + to + "}"); 
		}
		Expr simpliedIndexExpr = indexExpr.subs(from, to).simplify();
		if(simpliedIndexExpr instanceof Symbol || simpliedIndexExpr instanceof SymReal)
			return new Symbol(this.namePrefix.getLabel() + "_" + simpliedIndexExpr.getLabel());
		else
			return new Symbol(this.namePrefix.getLabel() + "_{" + simpliedIndexExpr.getLabel() + "}");
	}
	
	@Override
	public Expr diff(Expr expr) {
		if(Utils.symCompare(this, expr))
			return Symbol.C1;
		return Symbol.C0;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Symbols) {
			return this.namePrefix.equals(((Symbols) other).namePrefix) && 
					Utils.symCompare(this.indexExpr, ((Symbols) other).indexExpr);
		}
		return false;
	}

	@Override
	public Expr[] args() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeInfo getTypeInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateLabel() {
		if(indexExpr instanceof Symbol)
			this.label = namePrefix + "_" + indexExpr;
		else
			this.label = namePrefix + "_{" + indexExpr+"}";
		sortKey = label;
	}

}
