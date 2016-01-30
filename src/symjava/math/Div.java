package symjava.math;

import java.util.ArrayList;
import java.util.List;

import symjava.matrix.SymVector;
import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.arity.NaryOp;
import symjava.symbolic.utils.Utils;

/**
 * Divergence Operator
 * 
 */
public class Div extends NaryOp {
	protected Expr expr;

	public Div(SymVector vec) {
		super(null);
		if(vec instanceof Grad) {
			Grad g = (Grad)vec;
			if(g.isAbstract()) {
				this.args = g.getData();
				this.label = "div(" +g+ ")";
				this.sortKey = this.label;
				return;
			}
		}
		List<Expr> freeVars = Utils.extractSymbols(vec.getData());
		create(vec, freeVars.toArray(new Expr[0]));
	}
	
	public Div(SymVector vec, Expr... freeVars) {
		super(null);
		create(vec, freeVars);
	}
	
	private Div create(SymVector vec, Expr... freeVars) {
		if(vec.dim() != freeVars.length) {
			throw new RuntimeException("vec.dim() != freeVars.length");
		}
		this.args = vec.getData();
		
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<this.args.length; i++) {
			list.add(vec.get(i).diff(freeVars[i]));
		}
		this.expr = Utils.addListToExpr(list).simplify();
		this.label = expr.getLabel();
		this.sortKey = expr.getSortKey();
		return this;
	}
	
	public static Div apply(SymVector vec) {
		return new Div(vec);
	}
	
	public static Div apply(SymVector vec, Expr ...expr) {
		return new Div(vec, expr);
	}
	
	@Override
	public boolean isAbstract() {
		return expr == null;
	}
	
	public Expr getExpr() {
		return this.expr;
	}

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
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
		// TODO Auto-generated method stub
		
	}
	
}
