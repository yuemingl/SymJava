package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.Expr.TYPE;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

public class LCReturn extends LCBase {
	protected Expr arg;
	public LCReturn() {
		arg = null;
	}
	
	public LCReturn(Expr expr) {
		this.arg = expr;
		updateLabel();
	}
	
	public LCReturn(double expr) {
		this.arg = expr;
		updateLabel();
	}
	
	public void updateLabel() {
		this.label = this.indent + "return " + arg;
		
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(arg == null)
			il.append(InstructionConstants.RETURN);
		
		InstructionHandle startPos = arg.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg.getType() == TYPE.VECTOR) {
			//Copy results to outAry
			LocalVariableGen lg = mg.addLocalVariable("l_ret_len",
					Type.INT, null, null);
			int idx = lg.getIndex();
			il.append(factory.createInvoke("Jama.Matrix", "getColumnPackedCopy",
					new ArrayType(Type.DOUBLE,1), new Type[] {},
					Constants.INVOKEVIRTUAL));
			il.append(InstructionConstants.DUP);
			il.append(InstructionConstants.ARRAYLENGTH);
			lg.setStart(il.append(new ISTORE(idx)));
			
			il.append(new PUSH(cp, 0));
			il.append(InstructionConstants.ALOAD_1); //outAry (output buffer)
			il.append(InstructionConstants.ILOAD_2); //outPos (start position of output buffer)
			il.append(new ILOAD(idx));
			//Call System.arraycopy(src, srcPos, dest, destPos, length);
			il.append(factory.createInvoke("java.lang.System", "arraycopy",
					Type.VOID, new Type[] { Type.OBJECT, Type.INT, Type.OBJECT, Type.INT, Type.INT },
					Constants.INVOKESTATIC));
			/*
	        33: invokevirtual #20                 // Method Jama/Matrix.getColumnPackedCopy:()[D
	        36: dup           
	        37: arraylength   
	        38: istore        6
	        40: iconst_0      
	        41: aload_1       
	        42: iload_2       
	        43: iload         6
	        45: invokestatic  #26                 // Method java/lang/System.arraycopy:(Ljava/lang/Object;ILjava/lang/Object;II)V
              LocalVariableTable:
        Start  Length  Slot  Name   Signature
               0      49     0  this   Lsymjava/bytecode/Multiply1451587171120;
               0      49     1 outAry   [D
               0      49     2 outPos   I
               0      49     3  args   [[D
              11      38     4   l_A   LJama/Matrix;
              26      23     5   l_x   LJama/Matrix;
              38      11     6 l_ret_len   I

	        */
			return startPos;
		}		
		TYPE ty = arg.getType();
		if(ty == TYPE.DOUBLE)
			il.append(InstructionConstants.DRETURN);
		else if(ty == TYPE.INT)
			il.append(InstructionConstants.IRETURN);
		else if(ty == TYPE.LONG)
			il.append(InstructionConstants.LRETURN);
		else if(ty == TYPE.FLOAT)
			il.append(InstructionConstants.FRETURN);
		else
			il.append(InstructionConstants.RETURN);
		return startPos;
	}

	@Override
	public Expr[] args() {
		return new Expr[]{arg};
	}

	@Override
	public TypeInfo getTypeInfo() {
		return arg.getTypeInfo();
	}
}
