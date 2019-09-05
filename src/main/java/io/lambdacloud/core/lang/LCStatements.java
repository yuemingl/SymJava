package io.lambdacloud.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.TypeInfo;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class LCStatements extends LCBase {
	List<Expr> exprList = new ArrayList<Expr>();
	
	public LCStatements() {
		
	}
	
	public LCStatements(List<Expr> exprList) {
		this.exprList.addAll(exprList);
		updateLabel();
	}
	
	public LCStatements append(List<Expr> exprList) {
		this.exprList.addAll(exprList);
		updateLabel();
		return this;
	}
	
	public LCStatements append(Expr expr) {
		exprList.add(expr);
		updateLabel();
		return this;
	}
	
	public void updateLabel() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : exprList) {
			if(e instanceof LCBase) {
				sb.append(indent).append(e).append("\n");
			} else
				sb.append(indent).append(e).append(";\n");
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
	
	@Override
	public TypeInfo getTypeInfo() {
		if(this.exprList.size() > 0) {
			Expr last = this.exprList.get(this.exprList.size()-1);
			return last.getTypeInfo();
		}
		return null;
	}	
}
