package io.lambdacloud.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.lambdacloud.symjava.relational.Lt;
import io.lambdacloud.symjava.relational.Relation;
import io.lambdacloud.symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class LCLoop extends LCBase {
	Expr initExpr;
	Expr conditionExpr;
	Expr incrementExpr;
	List<Expr> bodyList = new ArrayList<Expr>();
	// Store position for 'break' instruction
	List<InstructionHandle> breakPos = new ArrayList<InstructionHandle>();
	
	public LCLoop(Expr conditionExpr) {
		this.conditionExpr = conditionExpr;
		updateLabel();
	}
	
	public LCLoop(Expr initExpr, Expr conditionExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
		updateLabel();
	}
	
	public LCLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		this.initExpr = initExpr;
		this.conditionExpr = conditionExpr;
		this.incrementExpr = incrementExpr;
		updateLabel();
	}
	
	public LCLoop appendBody(Expr expr) {
		if(expr instanceof LCBase) {
			((LCBase) expr).setParent(this).indent();
		}
		bodyList.add(expr);
		updateLabel();
		return this;
	}
	
	public LCLoop breakIf(Expr condition) {
		LCIf ifa = new LCIf(condition);
		ifa.setParent(this);
		ifa.appendTrue(new LCBreak());
		appendBody(ifa);
		updateLabel();
		return this;
	}
	
	public void addBreakPos(InstructionHandle pos) {
		this.breakPos.add(pos);
	}
	
	public void updateLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(indent).append("for(");
		if(initExpr != null) sb.append(initExpr);
		sb.append("; ");
		if(conditionExpr != null) sb.append(conditionExpr);
		sb.append("; ");
		if(incrementExpr != null) sb.append(incrementExpr);
		sb.append(") {\n");
		for(Expr e : bodyList) {
			if(e instanceof LCBase) {
				sb.append(indent).append(e).append("\n");
			} else
				sb.append(indent).append("\t").append(e).append(";\n");
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
		if(initExpr != null)
			loopStart = initExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		
		InstructionHandle bodyStart = null; //il.append(new NOP()); // Mark loop start position
		for(int i=0; i<bodyList.size(); i++) {
			Expr be = bodyList.get(i);
			InstructionHandle pos = be.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(bodyStart == null) bodyStart = pos;
		}
		if(incrementExpr != null) {
			InstructionHandle pos = incrementExpr.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
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

		InstructionHandle loopEnd = il.append(InstructionConstants.NOP);
		for(int i=0; i<breakPos.size(); i++) {
			il.insert(breakPos.get(i), new GOTO(loopEnd));
		}
		
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
