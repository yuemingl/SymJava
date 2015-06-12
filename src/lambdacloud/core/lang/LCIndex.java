package lambdacloud.core.lang;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.Expr.TYPE;

public class LCIndex extends LCBase {
	protected Symbol arrayRef;
	protected Expr index;
	public LCIndex(Symbol array, Expr index) {
		this.arrayRef = array;
		this.index = index;
		this.label = array + "["+index+"]";
		this.sortKey = label;
	}
	public LCIndex(Symbol array, int index) {
		this.arrayRef = array;
		this.index = index;
		this.label = array + "["+index+"]";
		this.sortKey = label;
	}
	
	public Symbol getArrayRef() {
		return arrayRef;
	}
	
	public Expr getIndex() {
		return index;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = il.append(new ALOAD(argsStartPos));
		il.append(new PUSH(cp, argsMap.get(arrayRef.getLabel())));
		il.append(InstructionConstants.AALOAD);
		index.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(InstructionConstants.DALOAD);
		
		return startPos;
	}
	
	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}
	
	@Override
	public Expr[] args() {
		return new Expr[]{arrayRef, index};
	}

}
