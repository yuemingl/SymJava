package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;


public class LCBreak extends LCBase {
	public LCBreak() {
		updateLabel();
	}
	
	public void updateLabel() {
		this.label = this.indent + "break;";
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		boolean find = false;
		InstructionHandle pos = null;
		LCBase tmp = this;
		while(tmp.parent != null) {
			if(tmp.parent instanceof LCLoop) {
				pos = il.append(InstructionConstants.NOP);
				((LCLoop)tmp.parent).addBreakPos(pos);
				find = true;
				break;
			}
			tmp = tmp.parent;
		}
		if(!find)
			throw new RuntimeException();
		return pos;
		
	}	
}
