package lambdacloud.core.operators;

import java.util.Map;

import lambdacloud.core.CloudBase;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.utils.Utils;

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

public class OPAsign extends CloudBase {
	protected Expr lhs;
	protected Expr rhs;
	public OPAsign(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public void compile() {
		
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(lhs instanceof CloudVar))
			throw new RuntimeException();
		CloudVar var = (CloudVar)lhs;
		rhs.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		TYPE ty = lhs.getType();
		if(ty == TYPE.DOUBLE)
			return il.append(new DSTORE(var.getLVTIndex()));
		if(ty == TYPE.INT)
			return il.append(new ISTORE(var.getLVTIndex()));
		if(ty == TYPE.LONG)
			return il.append(new LSTORE(var.getLVTIndex()));
		if(ty == TYPE.FLOAT)
			return il.append(new FSTORE(var.getLVTIndex()));
		return il.append(new ISTORE(var.getLVTIndex()));
	}
}
