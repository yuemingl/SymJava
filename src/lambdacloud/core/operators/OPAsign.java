package lambdacloud.core.operators;

import java.util.Map;

import lambdacloud.core.CloudBase;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.Utils;

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

public class OPAsign extends CloudBase {
	protected Expr lhs;
	protected Expr rhs;
	public OPAsign(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.label = lhs + " = " + rhs;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(lhs instanceof Symbol)) //allow symbol
			throw new RuntimeException(lhs.toString());
		InstructionHandle startPos = null;
		if(lhs instanceof CloudVar) {
			startPos = rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			CloudVar var = (CloudVar)lhs;
			TYPE ty = lhs.getType();
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
			il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(lhs.getLabel())));
			startPos = rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(InstructionConstants.DASTORE);
		}
		return startPos;
	}
	
	@Override
	public Expr[] args() {
		return new Expr[]{lhs, rhs};
	}	
}
