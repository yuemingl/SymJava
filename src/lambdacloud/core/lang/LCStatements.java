package lambdacloud.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

import symjava.relational.Lt;
import symjava.relational.Relation;
import symjava.symbolic.Expr;

public class LCStatements extends LCBase {
	List<Expr> exprList = new ArrayList<Expr>();
	
	public LCStatements append(Expr expr) {
		exprList.add(expr);
		initLabel();
		return this;
	}
	
	protected void initLabel() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : exprList) {
			sb.append(e).append(";\n");
		}
		this.label = sb.toString();
	}
	
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle loopStart = null;
		for(int i=0; i<exprList.size(); i++) {
			Expr be = this.exprList.get(i);
			InstructionHandle pos = be.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(loopStart == null) loopStart = pos;
		}
		return loopStart;
	}
	
	@Override
	public Expr[] args() {
		return this.exprList.toArray(new Expr[0]);
	}
}
