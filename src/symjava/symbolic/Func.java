package symjava.symbolic;

import java.util.List;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.utils.BytecodeUtils;

public class Func extends Expr {
	public Expr expr;
	public Symbol[] args;

	/**
	 * Construct an abstract function
	 * @param name
	 * @param args
	 */
	public Func(String name, Symbol ...args) {
		this.label = name;
		this.args = args;
	}
	
	public Func(String name, Expr expr) {
		//Extract free variables from expr
		this.label = name;
		this.expr = expr;
		args = BytecodeUtils.extractSymbols(expr);
	}
	
	public String getName() {
		return label;
	}
	
	public BytecodeFunc toBytecodeFunc() {
		try {
			BytecodeUtils.genClass(this);
			return (BytecodeFunc)Class.forName("symjava.bytecode."+this.label).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		return label+"("+BytecodeUtils.joinName(args, ",")+")";
	}

	@Override
	public Expr diff(Expr expr) {
		return this.expr.diff(expr);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Func(this.label, this.expr.subs(from, to));
	}
	
	@Override
	public Expr simplify() {
		Func f = new Func(label, expr.simplify());
		f.args = this.args;
		return f;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Func) {
			Func o = (Func)other;
			if(expr.symEquals(o.expr)) {
				if(args.length != o.args.length)
					return false;
				for(int i=0; i<args.length; i++) {
					if(!args[i].symEquals(o.args[i]))
						return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		expr.flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		expr.flattenAdd(outList);
	}
}
