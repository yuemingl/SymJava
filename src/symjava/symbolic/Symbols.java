package symjava.symbolic;

import java.util.HashMap;

import symjava.symbolic.utils.Utils;

public class Symbols extends Expr {
	String namePrefix;
	Expr indexSymbol;
	HashMap<Integer, Symbol> cache = new HashMap<Integer, Symbol>();
	
	public Symbols(String namePrefix) {
		this.namePrefix = namePrefix;
		this.label = namePrefix + "_?";
		sortKey = label;
	}
	
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
				Symbol s = this.get(index.getVal());
				return s;
			}
			return new Symbol(namePrefix+"_"+to);
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
