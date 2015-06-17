package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.BytecodeUtils;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public class LCAssign extends LCBase {
	protected Expr lhs;
	protected Expr rhs;
	public LCAssign(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
		updateLabel();
	}
	
	public void updateLabel() {
		this.label = this.indent + lhs + " = " + rhs;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(lhs instanceof Symbol) && !(lhs instanceof LCArray)) //allow symbol
			throw new RuntimeException(lhs.toString());
		InstructionHandle startPos = null;
		if(lhs instanceof LCArray) {
			//TODO Support local array
			LCArray aryRef = (LCArray)lhs;
			startPos = il.append(new ALOAD(argsMap.get(aryRef.getArrayRef().getLabel())));
			aryRef.getIndex().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			BytecodeUtils.typeCase(il, rhs.getType(), TYPE.DOUBLE);
			il.append(InstructionConstants.DASTORE);
		} else if(lhs instanceof LCVar && ((LCVar)lhs).isLocalVar()) {
			startPos = rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			LCVar var = (LCVar)lhs;
			TYPE ty = lhs.getType();
			BytecodeUtils.typeCase(il, rhs.getType(), ty);
			if(ty == TYPE.DOUBLE)
				il.append(new DSTORE(var.getLVTIndex()));
			else if(ty == TYPE.INT)
				il.append(new ISTORE(var.getLVTIndex()));
			else if(ty == TYPE.LONG)
				il.append(new LSTORE(var.getLVTIndex()));
			else if(ty == TYPE.FLOAT)
				il.append(new FSTORE(var.getLVTIndex()));
			else
				il.append(new ISTORE(var.getLVTIndex()));
		} else {
			startPos = il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(lhs.getLabel())));
			rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(InstructionConstants.DASTORE);
		}
		return startPos;
	}
	
	@Override
	public Expr[] args() {
		return new Expr[]{lhs, rhs};
	}	
}
