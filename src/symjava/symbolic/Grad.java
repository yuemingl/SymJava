package symjava.symbolic;

import java.util.List;

import symjava.matrix.SymVector;
import symjava.symbolic.utils.Utils;

public class Grad extends SymVector {
	Func func = null;
	Expr[] args = null;
	
	public Grad(SymVector data, Expr[] args) {
		for(Expr e : data)
			this.data.add(e);
		this.args = args;
	}
	
	public Grad(Expr f) {
		if(f instanceof Func) {
			if(f.isAbstract()) {
				this.func = (Func)f;
				for(Expr x : this.func.args) {
					data.add(f.diff(x));
				}
			} else {
				for(Expr x : ((Func)f).args) {
					data.add(f.diff(x));
				}
			}
		} else {
			List<Symbol> args = Utils.extractSymbols(f);
			for(Symbol x : args) {
				data.add(f.diff(x));
			}
		}	
	}
	
	public Grad(Expr f, Expr[] args) {
		if(f instanceof Func) {
			if(f.isAbstract()) {
				this.func = (Func)f;
				for(Expr x : this.func.args) {
					data.add(f.diff(x));
				}
			} else {
				for(Expr x : ((Func)f).args) {
					data.add(f.diff(x));
				}
			}
		} else {
			for(Expr x : args) {
				data.add(f.diff(x));
			}
		}		
	}

	
	/**
	 * Functional Gradient
	 * 
	 * @param F
	 * @param fs
	 * @param dfs
	 */
	public Grad(Expr F, Expr[] fs, Expr[] dfs) {
		Func FF = null;
		if(F instanceof Func) {
			FF = (Func)F;
		} else {
			FF = new Func(F.toString(), F);
		}
		this.func = FF;
		for(int i=0; i<fs.length; i++) {
			if(fs[i] instanceof Func) {
				data.add(FF.fdiff(fs[i], dfs[i]));
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public static SymVector apply(Expr f) {
		return new Grad(f);
	}
	
	public static SymVector apply(Expr F, Expr[] fs, Expr[] dfs) {
		return new Grad(F, fs, dfs);
	}
	
	public Func getFunc() {
		return func;
	}
	
	public boolean isAbstract() {
		if(func != null)
			return func.isAbstract();
		return false;
	}
	
	public SymVector subs(Expr from, Expr to) {
		if(this.func == null) {
			return new Grad(super.subs(from, to), this.args);
		}
		if(this.func == this.func.subs(from, to))
			return this;
		return new Grad(this.func.subs(from, to), this.func.args);
	}
	
	public SymVector diff(Expr expr) {
		return new Grad(super.diff(expr), this.args);
	}
	
	public String getLabel() {
		if(this.func == null) {
			for(Expr e : this.data) {
				if(e instanceof Derivative) {
					return "\\nabla{"+((Derivative)e).func.toString()+"}";
				}
			}
			return super.toString();
		}
		return "\\nabla{"+func+"}";
	}
}
