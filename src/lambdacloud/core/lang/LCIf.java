package lambdacloud.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lambdacloud.core.CloudSD;
import symjava.relational.Eq;
import symjava.relational.Gt;
import symjava.relational.Relation;
import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class LCIf extends LCBase {
	
	Expr condition;
	List<Expr> trueStmts = new ArrayList<Expr>();
	List<Expr> falseStmts = new ArrayList<Expr>();
	public LCIf(Expr condition, CloudSD ...args) {
		this.condition = condition;
		updateLabel();
	}
	
	public void updateLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(indent).append("if( ").append(condition).append(" ) {\n");
		for(Expr e : trueStmts) {
			if(e instanceof LCBase) {
				sb.append(indent).append(e).append("\n");
			} else
				sb.append(indent).append("\t").append(e).append(";\n");
		}
		if(falseStmts.size() > 0) {
		sb.append("} else {\n");
		for(Expr e : falseStmts) {
			if(e instanceof LCBase) {
				sb.append(indent).append(e).append("\n");
			} else
				sb.append(indent).append("\t").append(e).append(";\n");
		}
		}
		sb.append(indent).append("}");
		this.label = sb.toString();
		
	}
	
	public LCIf appendTrue(Expr expr) {
		if(expr instanceof LCBase) {
			((LCBase) expr).setParent(this).indent();
		}
		trueStmts.add(expr);
		updateLabel();
		return this;
	}

	public LCIf appendFalse(Expr expr) {
		if(expr instanceof LCBase) {
			((LCBase) expr).setParent(this).indent();
		}
		falseStmts.add(expr);
		updateLabel();
		return this;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(!(condition instanceof Relation))
			throw new RuntimeException();
		Relation cond = (Relation)condition;
		InstructionHandle startPos = null;
		InstructionHandle endPos = null;
		if(cond instanceof Gt) { // l > r
			cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(InstructionConstants.DCMPL);
			
			InstructionHandle trueBranchStart = null;
			for(Expr te : trueStmts) {
				InstructionHandle pos = te.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
				if(trueBranchStart == null)
					trueBranchStart = pos;
			}
			if(trueBranchStart == null) trueBranchStart = il.append(InstructionConstants.NOP);
			
			InstructionHandle falseBranchStart = null;
			for(Expr fe : falseStmts) {
				InstructionHandle pos = fe.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
				if(falseBranchStart == null)
					falseBranchStart = pos;
			}
			if(falseBranchStart == null) falseBranchStart = il.append(InstructionConstants.NOP);

			
			endPos = il.append(InstructionConstants.NOP);
			il.insert(trueBranchStart, new IFLE(falseBranchStart));
			il.insert(falseBranchStart, new GOTO(endPos));
		} else if(cond instanceof Eq) { // l == r
			startPos = cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			
			InstructionHandle trueBranchStart = null;
			for(Expr te : trueStmts) {
				InstructionHandle pos = te.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
				if(trueBranchStart == null)
					trueBranchStart = pos;
			}
			if(trueBranchStart == null) trueBranchStart = il.append(InstructionConstants.NOP);
			
			InstructionHandle falseBranchStart = null;
			for(Expr fe : falseStmts) {
				InstructionHandle pos = fe.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
				if(falseBranchStart == null)
					falseBranchStart = pos;
			}
			if(falseBranchStart == null) falseBranchStart = il.append(InstructionConstants.NOP);
			
			endPos = il.append(InstructionConstants.NOP);
			il.insert(trueBranchStart, new IF_ICMPNE(falseBranchStart));
			il.insert(falseBranchStart, new GOTO(endPos));
		}
		return startPos;
	}

	@Override
	public Expr[] args() {
		List<Expr> ret = new ArrayList<Expr>();
		ret.add(condition);
		ret.addAll(this.trueStmts);
		ret.addAll(this.falseStmts);
		return ret.toArray(new Expr[0]);
	}
}
