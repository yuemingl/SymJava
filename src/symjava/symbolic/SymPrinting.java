package symjava.symbolic;

public class SymPrinting {
	public static int getPrecedence(Expr expr) {
		if(expr instanceof Sum)
			return 5;
		else if(expr instanceof Add || expr instanceof Subtract)
			return 10;
		if(expr instanceof Multiply || expr instanceof Divide || expr instanceof Negate)
			return 20;
		if( expr instanceof Reciprocal )
			return 25;
		if(expr instanceof Power)
			return 30;
		if(expr instanceof Func) {
			Func f = (Func)expr;
			if(f.isAbstract())
				return 90;
			else
				return getPrecedence(f.getExpr());
		}
		if(expr instanceof Symbol || expr instanceof Symbols || expr instanceof SymReal<?>)
			return 100;
		return 1000; //default to atomic
	}
	
	public static String addParenthsesIfNeeded(Expr toMe, Expr forOperation) {
		if(getPrecedence(toMe) < getPrecedence(forOperation))
			return "(" + toMe.toString() + ")";
		return toMe.toString();
	}
	
	public static String addParenthsesIfNeeded2(Expr toMe, Expr forOperation) {
		if(getPrecedence(toMe) <= getPrecedence(forOperation))
			return "(" + toMe.toString() + ")";
		return toMe.toString();
	}
	
	public static String join(Expr[] exprs,String deliminator) {
		StringBuilder sb = new StringBuilder();
		for(Expr e : exprs) {
			sb.append(e.toString());
			sb.append(deliminator);
		}
		return sb.substring(0, sb.length()-deliminator.length());
	}
}
