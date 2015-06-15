package lambdacloud.core.lang;

import java.util.Map;

import symjava.symbolic.Expr;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public abstract class LCArray extends LCVar {
	protected LCArray arrayRef;
	protected Expr index;
	
	public LCArray(String name) {
		super(name);
		arrayRef = null;
		index = null;
	}
	
	public LCArray(LCArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	public LCArray(LCArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	public LCArray getArrayRef() {
		return arrayRef;
	}
	
	public Expr getIndex() {
		return index;
	}
	
	public LCLength getLength() {
		return new LCLength(this);
	}
	
	public LCLength size() {
		return new LCLength(this);
	}
	
	public String getName() {
		LCArray tmp = this;
		while(tmp.arrayRef != null) {
			tmp = tmp.arrayRef;
		}
		return tmp.getLabel();
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = null;
		if(this.isLocalVar()) {
			startPos = il.append(new ALOAD(indexLVT));
			index.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			LCArray tmp = this;
			while(tmp.arrayRef.index != null) {
				il.append(InstructionConstants.AALOAD);
				il.append(new ALOAD(tmp.arrayRef.indexLVT));
				tmp.arrayRef.index.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
				tmp = tmp.arrayRef;
			}
			TYPE ty = this.getType();
			if(ty == TYPE.DOUBLE)
				return il.append(InstructionConstants.DALOAD);
			if(ty == TYPE.INT)
				return il.append(InstructionConstants.IALOAD);
			if(ty == TYPE.LONG)
				return il.append(InstructionConstants.LALOAD);
			if(ty == TYPE.FLOAT)
				return il.append(InstructionConstants.FALOAD);
			return il.append(InstructionConstants.IALOAD);
		} else {
			// Load from arguments
			startPos = il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(arrayRef.getLabel())));
			il.append(InstructionConstants.AALOAD);
			index.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(InstructionConstants.DALOAD);
		}
		
		return startPos;
	}

	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}

	public abstract LCArray get(int index);

	public abstract LCArray get(Expr index);

//	public abstract LCArray set(int index);
//
//	public abstract LCArray set(Expr index);

	@Override
	public Expr[] args() {
		return new Expr[]{arrayRef, index};
	}
	
	public static LCIntArray getIntArray(String name) {
		return new LCIntArray(name);
	}
	
	public static LCLongArray getLongArray(String name) {
		return new LCLongArray(name);
	}
	
	public static LCFloatArray getFloatArray(String name) {
		return new LCFloatArray(name);
	}
	
	public static LCDoubleArray getDoubleArray(String name) {
		return new LCDoubleArray(name);
	}
	
	public static LCShortArray getShortArray(String name) {
		return new LCShortArray(name);
	}
	
	public static LCCharArray getCharArray(String name) {
		return new LCCharArray(name);
	}
	
	public static LCByteArray getByteArray(String name) {
		return new LCByteArray(name);
	}
}
