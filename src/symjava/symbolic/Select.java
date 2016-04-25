package symjava.symbolic;

public class Select extends Expr {
	
	public Select(Expr ...cols) {
		
	}
	
	public Select from(Expr ...tables) {
		return this;
	}
	
	public Select where() {
		return this;
	}
	
	public Select groupBy(Expr ...cols) {
		return this;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
	public Expr[] args() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expr diff(Expr x) {
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
