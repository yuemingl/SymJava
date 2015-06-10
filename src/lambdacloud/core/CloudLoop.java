package lambdacloud.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import symjava.relational.Lt;
import symjava.relational.Relation;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class CloudLoop extends CloudBase {
	Expr initExpr;
	Expr conditionExpr;
	Expr incrementExpr;
	List<Expr> bodyList = new ArrayList<Expr>();
	
	public CloudLoop(Expr conditionExpr) {
		this.conditionExpr = conditionExpr;
		initLabel();
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
		initLabel();
	}
	
	public CloudLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
		this.incrementExpr = incrementExpr;
		initLabel();
	}
	
	public CloudLoop appendBody(Expr expr) {
		bodyList.add(expr);
		initLabel();
		return this;
	}
	
	protected void initLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("for(");
		if(initExpr != null) sb.append(initExpr);
		sb.append("; ");
		if(conditionExpr != null) sb.append(conditionExpr);
		sb.append("; ");
		if(incrementExpr != null) sb.append(incrementExpr);
		sb.append(") {\n");
		for(Expr e : bodyList) {
			sb.append("\t").append(e).append(";\n");
		}
		sb.append("}");
		this.label = sb.toString();
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(conditionExpr instanceof Relation))
			throw new RuntimeException();
		Relation cond = (Relation)conditionExpr;

		InstructionHandle loopStart = null;
		if(this.initExpr != null)
			loopStart = this.initExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		
		InstructionHandle bodyStart = null; //il.append(new NOP()); // Mark loop start position
		for(int i=0; i<bodyList.size(); i++) {
			Expr be = this.bodyList.get(i);
			InstructionHandle pos = be.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(bodyStart == null) bodyStart = pos;
		}
		if(this.incrementExpr != null) {
			InstructionHandle pos = this.incrementExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(bodyStart == null) bodyStart = pos;
		}

		InstructionHandle cmpStart = null; //il.append(new NOP()); // Mark comparison start position
		if(cond instanceof Lt) { // l < r
			cmpStart = cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(new IF_ICMPLT(bodyStart));
		} //else if (...)
		
		if(bodyStart != null)
			il.insert(bodyStart, new GOTO(cmpStart)); // goto comparison before the loop
		
		if(loopStart == null) loopStart = bodyStart;
		if(loopStart == null) loopStart = cmpStart;
		return loopStart;
	}
	
	@Override
	public Expr[] args() {
		List<Expr> ret = new ArrayList<Expr>();
		ret.add(this.initExpr);
		ret.add(this.conditionExpr);
		ret.add(this.incrementExpr);
		ret.addAll(this.bodyList);
		return ret.toArray(new Expr[0]);
	}
}
