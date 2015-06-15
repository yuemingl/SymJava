package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * The base type of the local variables in a compiled function.
 * Particular type of a local variable is defined in a sub-class of CloudVar.
 * <br>
 * 
 * The local variables are exactly the same as the local variables in a Java function
 * that is used to store temporary values.
 *  
 */
public abstract class LCVar extends Symbol {
//	protected boolean isDeclaredAsLocal = false;
	protected int indexLVT = -1; // index in local variable table
	
	public LCVar(String name) {
		super(name);
//		this.isDeclaredAsLocal = true;
	}
	
	public Expr assign(Expr expr) {
		return new LCAssign(this, expr);
	}
	
	public Expr assign(double val) {
		return new LCAssign(this, Expr.valueOf(val));
	}

	public Expr assign(int val) {
		return new LCAssign(this, Expr.valueOf(val));
	}
	
	public void setLVTIndex(int index) {
		this.indexLVT = index;
	}
	
	public int getLVTIndex() {
		return this.indexLVT;
	}
	
	public String getName() {
		return this.getLabel();
	}
	
	public boolean isLocalVar() {
		return this.indexLVT >= 0;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(this.isLocalVar()) {
			// Load from local variable table
			TYPE ty = this.getType();
			if(ty == TYPE.DOUBLE)
				return il.append(new DLOAD(indexLVT));
			if(ty == TYPE.INT)
				return il.append(new ILOAD(indexLVT));
			if(ty == TYPE.LONG)
				return il.append(new LLOAD(indexLVT));
			if(ty == TYPE.FLOAT)
				return il.append(new FLOAD(indexLVT));
			return il.append(new ILOAD(indexLVT));
		} else {
			// Load from a double array from arguments
			il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(this.label)));
			return il.append(InstructionConstants.DALOAD);
		}
	}
	
	public static LCInt getInt(String name) {
		return new LCInt(name);
	}
	
	public static LCLong getLong(String name) {
		return new LCLong(name);
	}
	
	public static LCFloat getFloat(String name) {
		return new LCFloat(name);
	}
	
	public static LCDouble getDouble(String name) {
		return new LCDouble(name);
	}
	
	public static LCShort getShort(String name) {
		return new LCShort(name);
	}
	
	public static LCChar getChar(String name) {
		return new LCChar(name);
	}
	
	public static LCByte getByte(String name) {
		return new LCByte(name);
	}
}
