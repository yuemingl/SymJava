package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Derivative extends Func {
	Func func = null;
	List<Expr> dxyz = new ArrayList<Expr>();
	
	public Derivative(Func f, Expr x) {
		super("", f.args);
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
	
	protected String getDxyzLabel() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : dxyz) {
			sb.append(e.toString());
		}
		return sb.toString();
	}

	@Override
	public Expr diff(Expr expr) {
		return new Derivative(this, expr);
	}

	@Override
	public Expr simplify() {
		this.expr.simplify();
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Derivative) {
			Derivative o = (Derivative)other;
			if(this.expr == null && o.expr != null)
				return false;
			if(this.expr != null && o.expr == null)
				return false;
			if( (this.expr == null && o.expr == null) ||
				(Utils.symCompare(this.expr, o.expr)) ){
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
		if(expr != null)
			expr.subs(from, to);
		return this;
	}

}