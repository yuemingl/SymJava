package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Derivative extends Func {
	Func func = null;
	List<Expr> dxyz = new ArrayList<Expr>();
	
	public Derivative(Func f, Expr x) {
		super("", f.args);
		//Member variable 'expr' in func here is the expression after taking derivative
		if(f.expr != null)
			this.expr = f.expr.diff(x);
		this.func = f;
		this.dxyz.add(x);
		this.label = "D" + f.label + "D" + getDxyzLabel();
		this.sortKey = label;
	}
	
	public Derivative(Derivative df, Expr x) {
		super("", df.args);
		if(df.expr != null)
			this.expr = df.expr.diff(x);
		this.func = df.func;
		this.dxyz.addAll(df.dxyz);
		this.dxyz.add(x);
		this.label = df.label + x.label;
		this.sortKey = label;
	}
	
	public Derivative(Func f, Expr[] xs) {
		super("", f.args);
		if(f.expr != null) {
			this.expr = f.expr;
			for(Expr x : xs)
				this.expr = this.expr.diff(x);
		}
		this.func = f;
		for(Expr x : xs)
			this.dxyz.add(x);
		this.label = "D" + f.label + "D" + getDxyzLabel();
		this.sortKey = label;
	}
//	public Derivative(Derivative other) {
//		super("", other.args);
//		this.expr = other.expr;
//		this.dxyz.addAll(other.dxyz);
//		this.func = other.func;
//		this.label = other.label;
//		this.sortKey = other.sortKey;		
//	}
	
	public static Expr simplifiedIns(Func f, Expr x) {
		if(f.expr == null)
			return new Derivative(f, x);
		return f.expr.diff(x);
	}
	
	protected String getDxyzLabel() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : dxyz) {
			sb.append(e.toString());
		}
		return sb.toString();
	}

	@Override
	public Expr diff(Expr expr) {
		if(this.expr != null)
			return this.expr.diff(expr);
		else if(Utils.symCompare(this, expr)) {
			return Symbol.C1;
		} else if(!this.containsArg(expr) && expr instanceof Symbol) {
			return Symbol.C0;
		}
		return new Derivative(this, expr);
	}

	@Override
	public Expr simplify() {
		if(this.expr != null) {
			return this.expr.simplify();
		}
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Derivative) {
			Derivative o = (Derivative)other;
			Boolean rlt = Utils.symCompareNull(this.expr, o.expr);
			if(rlt != null && rlt == false)
				return false;
			if(!this.label.equals(o.label))
				return false;
			if(this.args.length != o.args.length)
				return false;
			for(int i=0; i<this.args.length; i++) {
				if(!Utils.symCompare(this.args[i],o.args[i]))
					return false;
			}
			if(this.dxyz.size() != o.dxyz.size())
				return false;
			for(int i=0; i<this.dxyz.size(); i++) {
				if(!Utils.symCompare(this.dxyz.get(i), o.dxyz.get(i)))
					return false;
			}
			return true;
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

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		} else if(expr != null) {
			return expr.subs(from, to);
		} else if(Utils.symCompare(func, from)) {
			if(to instanceof Func) {
				Derivative rlt = new Derivative((Func)to, 
						this.dxyz.toArray(new Expr[0])
						);
				return rlt;
			} else {
				Expr diffExpr = to;
				for(Expr x : dxyz)
					diffExpr = diffExpr.diff(x);
				return diffExpr;
			}
		}
		return this;
	}
	
	public Func getFunc() {
		return func;
	}

}
