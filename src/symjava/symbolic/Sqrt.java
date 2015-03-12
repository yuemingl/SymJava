package symjava.symbolic;

public class Sqrt extends UnaryOp {
	public int root = 2;
	
	public Sqrt(Expr base) {
		super(base);
		label = "\\sqrt{" + base + "}";
		sortKey = base.getSortKey()+"sqrt[2]"+String.valueOf(root);
	}
	
	public Sqrt(Expr base, int root) {
		super(base);
		label = "\\sqrt["+root+"]{" + base + "}";
		sortKey = base.getSortKey()+"sqrt["+root+"]"+String.valueOf(root);
	}

	@Override
	public Expr diff(Expr expr) {
		return Power.simplifiedIns(base, 1.0/root).diff(expr);
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sqrt) {
			Sqrt o = (Sqrt)other;
			if(base.symEquals(o.base) && root == o.root)
				return true;
		}
		return false;
	}

}
