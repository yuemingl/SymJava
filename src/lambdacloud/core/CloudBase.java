package lambdacloud.core;

import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.symbolic.Expr;

public class CloudBase extends Expr {

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TYPE getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public int declareLocal(CloudVar var, MethodGen mg, InstructionList il) {
		//variable name
		//initial value
		//index in local variable table (LVT)
		if(var instanceof CloudInt) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.INT, null, null);
			int idx = lg.getIndex();
			//il.append(InstructionConstants.ICONST_0);
			//lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudLong) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.LONG, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.LCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
			
		} else if(var instanceof CloudFloat) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.FLOAT, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.FCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudDouble) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.DOUBLE, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.DCONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudBoolean) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.BOOLEAN, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudChar) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.CHAR, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudByte) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.BYTE, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		} else if(var instanceof CloudShort) {
			LocalVariableGen lg = mg.addLocalVariable(var.getLabel(),
					Type.SHORT, null, null);
			int idx = lg.getIndex();
			il.append(InstructionConstants.ICONST_0);
			lg.setStart(il.append(new DSTORE(idx)));
			return idx;
		}
		throw new RuntimeException();
	}
}
