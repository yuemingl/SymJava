package symjava.symbolic;

import java.util.Map;

import symjava.symbolic.arity.NaryOp;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.AASTORE;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Concatenate symbol matrices or vectors with the same dimension(s)
 * 
 */
public class Concat extends NaryOp {

	public Concat(Expr ...args) {
		super(args);
		updateLabel();
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
	public Expr diff(Expr x) {
		return null;
	}

	@Override
	public void updateLabel() {
		this.label = "["+Utils.joinLabels(args, ", ")+"]";
		this.sortKey = this.label;
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = null;
		//if(arg1.getType() == TYPE.MATRIX && arg2.getType() == TYPE.MATRIX) {
		il.append(new PUSH(cp, args.length));
		il.append(new ANEWARRAY(cp.addClass("Jama.Matrix")));
		for(int i=0; i<args.length; i++) {
			il.append(InstructionConstants.DUP);
			il.append(new PUSH(cp, i));
			InstructionHandle tmp = args[i].bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			if(startPos == null) startPos = tmp;
			il.append(new AASTORE());
		}
		il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "concat",
				new ObjectType("Jama.Matrix"), new Type[] { new ArrayType(new ObjectType("Jama.Matrix"),1) },
				Constants.INVOKESTATIC));
		return startPos;
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		TypeInfo ti = new TypeInfo();
		ti.type = TYPE.VECTOR;
		ti.dim = new int[1];
		for(int i=0; i<args.length; i++)
			ti.dim[0] += args[i].getTypeInfo().dim[0];
		return ti;
	}	
}
