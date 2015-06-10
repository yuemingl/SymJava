package lambdacloud.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lambdacloud.core.CSD;
import symjava.relational.Gt;
import symjava.relational.Relation;
import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class LCIf extends LCBase {
	
	Expr condition;
	List<Expr> trueStmts = new ArrayList<Expr>();
	List<Expr> falseStmts = new ArrayList<Expr>();
	public LCIf(Expr condition, CSD ...args) {
		this.condition = condition;
		initLabel();
	}
	
	public void initLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("if( ").append(condition).append(" ) {\n");
		for(Expr e : trueStmts) {
			sb.append("\t").append(e).append(";\n");
		}
		sb.append("} else {\n");
		for(Expr e : falseStmts) {
			sb.append("\t").append(e).append(";\n");
		}
		sb.append("}");
		this.label = sb.toString();
		
	}
	
	public LCIf appendTrue(Expr expr) {
		trueStmts.add(expr);
		initLabel();
		return this;
	}

	public LCIf appendFalse(Expr expr) {
		falseStmts.add(expr);
		initLabel();
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
		InstructionHandle endif = null;
		if(cond instanceof Gt) { // l > r
			cond.lhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			cond.rhs().bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(InstructionConstants.DCMPL);
			
			InstructionHandle trueBranchStart = il.append(InstructionConstants.NOP);
			trueStmts.get(0).bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			//for ...
			
			InstructionHandle falseBranchStart = il.append(InstructionConstants.NOP);
			falseStmts.get(0).bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			//for ...
			
			endif = il.append(InstructionConstants.NOP);
			il.insert(trueBranchStart, new IFLE(falseBranchStart));
			il.insert(falseBranchStart, new GOTO(endif));
		}
		return endif;
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
