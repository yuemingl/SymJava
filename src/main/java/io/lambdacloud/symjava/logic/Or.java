package io.lambdacloud.symjava.logic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IOR;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Expr.TYPE;
import io.lambdacloud.symjava.symbolic.TypeInfo;
import io.lambdacloud.symjava.symbolic.arity.BinaryOp;

public class Or extends BinaryOp implements Logic {

	public Or(Expr l, Expr r) {
		super(l, r);
		this.label = l+" | "+r;
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
		return this;
	}
	public static Expr simplifiedIns(Expr lhs, Expr rhs) {
		return new Or(lhs, rhs);
	}
	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(new IOR());
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
