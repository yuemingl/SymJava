package symbolic;

public class SymPrinting {
	public static int getPrecedence(Expr expr) {
		if(expr instanceof Add || expr instanceof Subtract)
			return 10;
		if(expr instanceof Multiply || expr instanceof Divide || expr instanceof Negate)
			return 20;
		if( expr instanceof Reciprocal )
			return 25;
		if(expr instanceof Power)
			return 30;
		if(expr instanceof Symbol || expr instanceof Symbols || expr instanceof SymReal<?>)
			return 100;
		return 1;
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
}
