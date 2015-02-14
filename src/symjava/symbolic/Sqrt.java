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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

}
