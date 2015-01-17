package symjava.symbolic;

import java.util.List;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

public class Func extends Expr {
	public Expr expr = null;
	public Expr[] args = null;

	/**
	 * Construct an abstract function
	 * @param name
	 * @param args
	 */
	public Func(String name, Expr ...args) {
		this.label = name;
		this.args = args;
		this.sortKey = label;
	}
	
	public Func(String name, Expr expr) {
		//Extract free variables from expr
		this.label = name;
		this.expr = expr;
		args = BytecodeUtils.extractSymbols(expr);
		this.sortKey = label;
	}
	
	public String getName() {
		return label;
	}
	
	@Override
	public Expr getExpr() {
		return this.expr;
	}
	
	@Override
	public boolean isAbstract() {
		return expr == null;
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
		if(expr != null)
			return expr.toString();
		else
			return label+"("+BytecodeUtils.joinName(args, ",")+")";
	}
	
	public String getLabel() {
		return label+"("+BytecodeUtils.joinName(args, ",")+")";
	}

	@Override
	public Expr diff(Expr expr) {
		if(Utils.symCompare(this, expr)) {
			return Symbol.C1;
		} else if(this.containsArg(expr)) {
			return new Derivative(this, expr);
		} else {
			return Symbol.C0;
		}
	}
	
	public boolean containsArg(Expr arg) {
		if(arg != null) {
			for(Expr e : args) {
				boolean b = Utils.symCompare(e, arg);
				if(b) return true;
			}
		}
		return false;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		else if(this.expr != null)
			return new Func(this.label, this.expr.subs(from, to));
		else
			return this;
	}
	
	@Override
	public Expr simplify() {
		if(expr != null) {
			Func f = new Func(label, expr.simplify());
			f.args = this.args;
		}
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Func) {
			Func o = (Func)other;
			if(this.expr == null && o.expr != null)
				return false;
			if(this.expr != null && o.expr == null)
				return false;
			if(!this.label.equals(o.label))
				return false;
			if( (this.expr == null && o.expr == null) ||
				 Utils.symCompare(this.expr, o.expr) ) {
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
		if(expr != null)
			expr.flattenAdd(outList);
		else
			outList.add(this);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(expr != null)
			expr.flattenMultiply(outList);
		else
			outList.add(this);
	}
}
