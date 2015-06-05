package lambdacloud.core;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import lambdacloud.core.operators.OPAsign;
import lambdacloud.core.operators.OpIndex;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class CloudVar extends Symbol {
	protected boolean isDeclaredAsLocal = false;
	protected int indexLVT; // index in local variable table
	protected int varType; //0=int, 1=long, 2=float, 3=double
	
	public CloudVar(String name) {
		super(name);
		this.isDeclaredAsLocal = true;
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
		if(this.isDeclaredAsLocal) {
			// Load from a local variable
			return il.append(new DLOAD(indexLVT));
		} else {
			// Load from an array (argument or local array)
			il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(this.label)));
			return il.append(InstructionConstants.DALOAD);
		}
	}
}
