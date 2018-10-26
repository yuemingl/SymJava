package symjava.logic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IXOR;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.arity.UnaryOp;

public class Not extends UnaryOp implements Logic {

	public Not(Expr arg) {
		super(arg);
		this.label = "!"+arg;
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
	
	public static Expr simplifiedIns(Expr expr) {
		return new Not(expr);
	}
	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(new PUSH(cp, 1));
		il.append(new IXOR());
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
