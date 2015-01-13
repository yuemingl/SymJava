package symbolic;

import java.util.List;

import bytecode.BytecodeFunc;

public class Func extends Expr {
	public Expr expr;
	public Symbol[] args;

	/**
	 * Construct an abstract function
	 * @param name
	 * @param args
	 */
	public Func(String name, Symbol ...args) {
		this.name = name;
		this.args = args;
	}
	
	public Func(String name, Expr expr) {
		//Extract free variables from expr
		this.name = name;
		this.expr = expr;
		args = Utils.extractSymbols(expr);
	}
	
	public BytecodeFunc toBytecodeFunc() {
		try {
			Utils.genClass(this);
			return (BytecodeFunc)Class.forName("bytecode."+this.name).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		return name+"("+Utils.joinName(args, ",")+")";
	}

	@Override
	public Expr diff(Expr expr) {
		return this.expr.diff(expr);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Func(this.name, this.expr.subs(from, to));
	}
	
	@Override
	public Expr simplify() {
		Func f = new Func(name, expr.simplify());
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
	protected void flattenAdd(List<Expr> outList) {
		expr.flattenAdd(outList);
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		expr.flattenAdd(outList);
	}
}
