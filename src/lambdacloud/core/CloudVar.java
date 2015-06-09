package lambdacloud.core;

import java.util.Map;

import lambdacloud.core.operators.OPAsign;
import lambdacloud.core.operators.OpIndex;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

/**
 * The base type of the local variables in a compiled function.
 * Particular type of a local variable is defined in a sub-class of CloudVar.
 * <br>
 * 
 * The local variables are exactly the same as the local variables in a Java function
 * that is used to store temporary values.
 *  
 */
public abstract class CloudVar extends Symbol {
//	protected boolean isDeclaredAsLocal = false;
	protected int indexLVT; // index in local variable table
	
	public CloudVar(String name) {
		super(name);
//		this.isDeclaredAsLocal = true;
	}
	
	public Expr assign(Expr expr) {
		return new OPAsign(this, expr);
	}
	
	public Expr assign(double val) {
		return new OPAsign(this, Expr.valueOf(val));
	}

	public Expr assign(int val) {
		return new OPAsign(this, Expr.valueOf(val));
	}
	
	public Expr get(Expr index) {
		return new OpIndex(this, index);
	}
	
	public void setLVTIndex(int index) {
		this.indexLVT = index;
	}
	
	public int getLVTIndex() {
		return this.indexLVT;
	}
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
//		if(this.isDeclaredAsLocal) {
			// Load from a local variable
//			return il.append(new DLOAD(indexLVT));
//		} else {
//			// Load from an array (argument or local array)
//			il.append(new ALOAD(argsStartPos));
//			il.append(new PUSH(cp, argsMap.get(this.label)));
//			return il.append(InstructionConstants.DALOAD);
//		}
			
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
			
	}
}
