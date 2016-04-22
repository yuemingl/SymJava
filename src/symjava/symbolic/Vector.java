package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.matrix.SymVector;

public class Vector extends Tensor {
	public int nStart;
	public int nDim;
	public Vector parent;
	protected int indexLVT = -1;
	
	public Vector(String name, int nDim) {
		super(name);
		this.nStart = 0;
		this.nDim = nDim;
	}

	public Vector(Vector parent, int nStart, int nDim) {
		super(parent.label+"_"+nStart+"_"+nDim);
		this.nStart = nStart;
		this.nDim = nDim;
		this.parent = parent;
	}
	
	public Vector(Vector parent, String name, int nStart, int nDim) {
		super(name);
		this.nStart = nStart;
		this.nDim = nDim;
		this.parent = parent;
	}
	
	public SymVector split(int nBlock) {
		int n = nDim/nBlock;
		if(nDim%nBlock > 0)
			n = (nDim+(nBlock-nDim%nBlock))/nBlock;
		int last_n = nDim%n==0?n:nDim%n;
		//System.out.println(n);
		//System.out.println(last_n);
		Expr[] items = new Expr[nBlock];
		for(int j=0; j<nBlock-1; j++) {
			items[j] = new Vector(this, this.label+"_"+j, j*n, n);
		}
		items[nBlock-1] = new Vector(this, this.label+"_"+(nBlock-1), (nBlock-1)*n, last_n);
		return new SymVector(items);
	}

	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		if(indexLVT == -1) {
			//jama.Matrix l_m = null;
			LocalVariableGen lg = mg.addLocalVariable("l_"+getLabel(),
					new ObjectType("Jama.Matrix"), null, null);
			indexLVT = lg.getIndex();
//			il.append(InstructionConstants.ACONST_NULL);
//			lg.setStart(il.append(new DSTORE(idx)));

			// First time touch the matrix, declare a local reference of Java.Matrix
			il.append(new NEW(cp.addClass("Jama.Matrix")));
		    il.append(InstructionConstants.DUP);

		    //prepare argument: double[] vals
			il.append(new ALOAD(argsStartPos));
			il.append(new PUSH(cp, argsMap.get(this.label)));
			il.append(InstructionConstants.AALOAD); //Load double from array 
			
			//prepare argument: double m - number of rows
    		il.append(new PUSH(cp, nDim));
			il.append(factory.createInvoke("Jama.Matrix", "<init>",
				Type.VOID, new Type[] { new ArrayType(Type.DOUBLE, 1), Type.INT },
				Constants.INVOKESPECIAL));

			//jama.Matrix l_m = new jama.Matrix(args[], nRow);
			lg.setStart(il.append(new ASTORE(indexLVT)));
		}
		return il.append(new ALOAD(indexLVT));
		//il.append(new ALOAD(indexLVT));
		//il.append(new PUSH(cp, 1.0));
		//return il.append(InstructionConstants.DRETURN);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		TypeInfo ti = new TypeInfo(TYPE.VECTOR, new int[1]);
		ti.dim[0] = nDim;
		return ti;
	}
	
	public void bytecodeGenReset() {
		this.indexLVT = -1;
	}
	
	@Override
	public Expr getParent() {
		return this.parent;
	}
	
	public int dim() {
		return this.nDim;
	}
	
	public static void main(String[] args) {
		Vector v = new Vector("A",8);
		SymVector sv = v.split(3);
		System.out.println(sv);
	}
}
