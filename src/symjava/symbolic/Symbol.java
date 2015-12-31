package symjava.symbolic;

import java.util.Map;

import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * An object of Symbol has a string name which is its unique id,
 * just like the name of a variable in Java.
 * 
 * Two objects of Symbol with the same name are the same thing in SymJava. 
 * 
 * @see symEquals()
 * 
 */
public class Symbol extends Expr {
	public static Symbol a = new Symbol("a");
	public static Symbol b = new Symbol("b");
	public static Symbol c = new Symbol("c");
	public static Symbol d = new Symbol("d");
	public static Symbol e = new Symbol("e");
	public static Symbol f = new Symbol("f");
	public static Symbol g = new Symbol("g");
	public static Symbol h = new Symbol("h");
	
	public static Symbol r = new Symbol("r");
	public static Symbol s = new Symbol("s");
	public static Symbol t = new Symbol("t");
	
	public static Symbol u = new Symbol("u");
	public static Symbol v = new Symbol("v");
	public static Symbol w = new Symbol("w");
	
	public static Symbol x = new Symbol("x");
	public static Symbol y = new Symbol("y");
	public static Symbol z = new Symbol("z");
	
	public static Symbol phi = new Symbol("\\phi");
	public static Symbol psi = new Symbol("\\psi");
	public static Symbol chi = new Symbol("\\chi");
	
	public static Symbol alpha = new Symbol("\\alpha");
	public static Symbol beta = new Symbol("\\beta");
	public static Symbol gamma = new Symbol("\\gamma");
	
	public static SymInteger Cm2 = new SymInteger(-1);
	public static SymInteger Cm1 = new SymInteger(-1);
	public static SymInteger C0 = new SymInteger(0);
	public static SymInteger C1 = new SymInteger(1);
	public static SymInteger C2 = new SymInteger(2);
	
	public static Infinity oo = new Infinity();
	
	public Symbol(String name) {
		this.label = name;
		sortKey = label;
	}
	
	public String toString() {
		return label;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		}
		return this;
	}

	@Override
	public Expr diff(Expr expr) {
		if(this.symEquals(expr))
			return C1;
		return C0;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Symbol)
			return this.label.equals(other.label);
		return false;
	}
	
	public String getPrefix() {
		//TODO Fixit suport reduce
		if(this.toString().equals("_")||this.toString().equals("__"))
			return this.toString();
		String[] ss = this.toString().split("_");
		return ss[0];
	}
	
	public boolean containsSubIndex() {
		String[] ss = this.toString().split("_");
		if(ss.length == 2) {
			return true;
		}
		return false;
	}
	
	public int getSubIndex() {
		String[] ss = this.toString().split("_");
		if(ss.length == 2) {
			return Integer.valueOf(ss[1]);
		}
		throw new IllegalArgumentException(this.getLabel()+" contains no sub index.");
	}
	
//	/**
//	 * Declare the symbol as a local variable when compiling an expression which
//	 * contains this symbol. By default, a symbol is one of the arguments of 
//	 * an instance of SymFunc. The declaration changes this default behavior of 
//	 * the symbol. 
//	 * 
//	 * If a symbol is declared as a local variable it will be defined as 
//	 * a local variable in the compiled function. The result of evaluation of the 
//	 * associated expression is stored in this local variable.
//	 * 
//	 * @return
//	 */
//	public Expr declareAsLocal(Class<?> clazz) {
//		this.isDeclaredAsLocal = true;
//		return this;
//	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
//Note: local variable implementation has been moved to LCVar		
//		if(this.isDeclaredAsLocal) {
//			// Load from a local variable
//			return il.append(new DLOAD(indexLVT));
//		} else {
			// Load from an array (argument or local array)
			il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(this.label)));
			return il.append(InstructionConstants.DALOAD);
//		}
	}
	
	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}

	@Override
	public Expr[] args() {
		return new Expr[0];
	}

}
