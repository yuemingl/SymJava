package symjava.symbolic;

import java.util.HashMap;

import symjava.symbolic.utils.Utils;

/**
 * An object of Symbols can represents a list of symbols
 * For example:
 * Symbols ss = new Symbols("x"); //x_i, i=...,1,2,3...
 * Symbol x1 = ss.get(1); //x_1
 * Symbol x8 = ss.get(8); //x_8
 * 
 * This can be used to generate a list of symbols in summation or parameter list in an equation.
 * 
 * @author yuemingliu
 *
 */
public class Symbols extends Expr {
	String namePrefix;
	Expr indexSymbol;
	HashMap<Integer, Symbol> cache = new HashMap<Integer, Symbol>();
	
	/**
	 * x_i, i=...,1,2,3,...
	 * @param namePrefix
	 */
	public Symbols(String namePrefix) {
		this.namePrefix = namePrefix;
		this.indexSymbol = new Symbol("i");
		this.label = namePrefix + indexSymbol;
		sortKey = label;
	}
	
	/**
	 * 
	 * @param namePrefix
	 * @param indexSymbol
	 */
	public Symbols(String namePrefix, Expr indexSymbol) {
		this.namePrefix = namePrefix;
		this.indexSymbol = indexSymbol;
		this.label = namePrefix + "_" + indexSymbol;
		sortKey = label;
	}
	
	public Symbol get(int index) {
		Symbol s = cache.get(index);
		if(s == null) {
			s = new Symbol(namePrefix+"_"+index);
			cache.put(index, s);
		}
		return s;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		} else if(Utils.symCompare(indexSymbol,from)) {
			if(to instanceof SymInteger) {
				SymInteger index = (SymInteger)to;
				Symbol s = this.get(index.getValue());
				return s; //ss.subs(i,3): x_i => x_3
			}
			return new Symbol(namePrefix+"_"+to); //ss.subs(i,j): x_i => x_j
		}
		return this;
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
		//TODO May need better way to compare
		return other.label.startsWith(this.namePrefix);
	}

}
