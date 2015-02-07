package symjava.symbolic;

import java.util.List;

import symjava.matrix.SymVector;
import symjava.symbolic.utils.Utils;

/**
 * Gradient of a function or expression
 *
 */
public class Grad extends SymVector {
	public Expr[] args = null;
	protected Func func = null;
	
	/**
	 * Construct an instance directly from data and args
	 * 
	 * @param data
	 * @param args
	 */
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
			List<Expr> args = Utils.extractSymbols(f);
			for(Expr x : args) {
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
		if(fs.length != dfs.length)
			throw new IllegalArgumentException();
		
		if(F instanceof Func) {
			this.func = (Func)F;
		}
		for(int i=0; i<fs.length; i++) {
			if(fs[i] instanceof Func && dfs[i] instanceof Func) {
				data.add(F.fdiff(fs[i], dfs[i]));
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
		//if(Utils.symCompare(this, from)) {
		//	return to;
		//}
		if(this.func == null || this.func.isAbstract()) {
			return new Grad(super.subs(from, to), this.args);
		}
		Expr funcSub = this.func.subs(from, to);
		if(this.func != funcSub) {
			return new Grad(funcSub, this.func.args);
		}
		return this;
	}
	
	public SymVector diff(Expr expr) {
		return new Grad(super.diff(expr), this.args);
	}
	
	@Override
	public String toString() {
		if(this.func == null) {
			for(Expr e : this.data) {
				if(!(e instanceof Derivative)) {
					return super.toString();
				}
			}
			return "\\nabla{"+((Derivative)this.data.get(0)).func.toString()+"}";
		}
		return "\\nabla{"+this.func.getLabel()+"}";
	}
}
