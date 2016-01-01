package symjava.symbolic;

import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.DADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.FADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.IADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.LADD;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.arity.NaryOp;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

public class Concat extends NaryOp {

	public Concat(Expr ...args) {
		super(args);
		
		
		
		
		
	}

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
	public Expr diff(Expr x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TYPE getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateLabel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		return null;

	}
}
