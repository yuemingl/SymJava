package io.lambdacloud.symjava.relational;

import java.util.Map;

import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.TypeInfo;
import io.lambdacloud.symjava.symbolic.arity.BinaryOp;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * a <= b
 *
 */
public class Le extends BinaryOp implements Relation  {

	public Le(Expr arg1, Expr arg2) {
		super(arg1, arg2);
		this.label = arg1 + " <= " + arg2;
		this.sortKey = this.label;

	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Le apply(Expr lhs, Expr rhs) {
		return new Le(lhs, rhs);
	}
	public static Le apply(double lhs, Expr rhs) {
		return new Le(Expr.valueOf(lhs), rhs);
	}
	public static Le apply(Expr lhs, double rhs) {
		return new Le(lhs, Expr.valueOf(rhs));
	}
	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(new DCMPL());
		InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
		InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
		InstructionHandle nop = il.append(new NOP());
		il.insert(iconst1, new IFGT(iconst0));
		il.insert(iconst0, new GOTO(nop));
		return startPos;
	}	
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiInt;
	}

	@Override
	public void updateLabel() {
		// TODO Auto-generated method stub
		
	}	
	
}
