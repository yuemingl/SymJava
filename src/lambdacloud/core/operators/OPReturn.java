package lambdacloud.core.operators;

import java.util.Map;

import lambdacloud.core.CloudBase;
import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class OPReturn extends CloudBase {
	protected Expr arg;
	public OPReturn(Expr expr) {
		this.arg = expr;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		TYPE ty = arg.getType();
		if(ty == TYPE.DOUBLE)
			il.append(InstructionConstants.DRETURN);
		else if(ty == TYPE.INT)
			il.append(InstructionConstants.IRETURN);
		else if(ty == TYPE.LONG)
			il.append(InstructionConstants.LRETURN);
		else if(ty == TYPE.FLOAT)
			il.append(InstructionConstants.FRETURN);
		else
			il.append(InstructionConstants.RETURN);
		return startPos;
	}

	@Override
	public Expr[] args() {
		return new Expr[]{arg};
	}
}
