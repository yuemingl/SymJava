package io.lambdacloud.symjava.bytecode.bcel;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.symbolic.utils.FuncClassLoader;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DADD;
import com.sun.org.apache.bcel.internal.generic.DALOAD;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IINC;
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

public class ForLoop {

	public static void main(String[] args) {
		String packageName = "symjava.bytecode";
		String clsName = "TestForLoop";
		String fullClsName = packageName+"."+clsName;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"symjava.bytecode.BytecodeFunc"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);
		
		short acc_flags = ACC_PUBLIC;
		MethodGen mg = new MethodGen(acc_flags, // access flags
				Type.DOUBLE, // return type
				new Type[] { // argument types
					new ArrayType(Type.DOUBLE, 1) 
				}, 
				new String[] { "args" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		
		//double sum = 0;
		LocalVariableGen lg;
		lg = mg.addLocalVariable("sum",
				Type.DOUBLE, null, null);
		int idxSum = lg.getIndex();
		il.append(InstructionConstants.DCONST_0);
		lg.setStart(il.append(new DSTORE(idxSum))); // "sum" valid from here

		//////////////////////////////////////////////////////////////
		//	for(int i=0; i<10; i++) {
		//		sum += args[i];
		//	}
		/////////////////////////////////////////////////////////////
		//int i = 0;
		lg = mg.addLocalVariable("i",
				Type.INT, null, null);
		int idxI = lg.getIndex();
		il.append(InstructionConstants.ICONST_0);
		lg.setStart(il.append(new ISTORE(idxI))); // "i" valid from here
		
		//Loop body: sum = sum + args[i]
		InstructionHandle loopStart = il.append(new ALOAD(1));
		il.append(new ILOAD(idxI));
		il.append(new DALOAD());
		il.append(new DLOAD(idxSum));
		il.append(new DADD());
		il.append(new DSTORE(idxSum));
		
		//i++
		il.append(new IINC(idxI, 1));

		//Compare: i < 10
		InstructionHandle loopCmp = il.append(new ILOAD(idxI));
		il.append(new PUSH(cp, 10));
		il.append(new IF_ICMPLT(loopStart));
		
		il.insert(loopStart, new GOTO(loopCmp));
		/////////////////////////////////////////////////////////////

		il.append(new DLOAD(idxSum));
		il.append(InstructionConstants.DRETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		FuncClassLoader<BytecodeFunc> fcl = new FuncClassLoader<BytecodeFunc>();
		BytecodeFunc fun = fcl.newInstance(cg);
		double[] params = new double[10];
		for(int i=0; i<params.length; i++)
			params[i] = i;
		System.out.println(fun.apply(params));
		
		try {
			cg.getJavaClass().dump("bin/symjava/bytecode/"+clsName+".class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}
	}
}
/**
$ javap -verbose TestForLoop.class 
Classfile /home/yliu/workspace_java/SymJava/bin/symjava/bytecode/TestForLoop.class
  Last modified Apr 9, 2015; size 441 bytes
  MD5 checksum 0c83fefac38f7c2a4f3ac482d6219cde
  Compiled from "<generated>"
public class symjava.bytecode.TestForLoop implements symjava.bytecode.BytecodeFunc
  SourceFile: "<generated>"
  minor version: 3
  major version: 45
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Utf8               SourceFile
   #2 = Utf8               <generated>
   #3 = Utf8               symjava/bytecode/TestForLoop
   #4 = Class              #3             //  symjava/bytecode/TestForLoop
   #5 = Utf8               java/lang/Object
   #6 = Class              #5             //  java/lang/Object
   #7 = Utf8               apply
   #8 = Utf8               ([D)D
   #9 = Utf8               this
  #10 = Utf8               Lsymjava/bytecode/TestForLoop;
  #11 = Utf8               args
  #12 = Utf8               [D
  #13 = Utf8               sum
  #14 = Utf8               D
  #15 = Utf8               i
  #16 = Utf8               I
  #17 = Utf8               LocalVariableTable
  #18 = Utf8               Code
  #19 = Utf8               <init>
  #20 = Utf8               ()V
  #21 = NameAndType        #19:#20        //  "<init>":()V
  #22 = Methodref          #6.#21         //  java/lang/Object."<init>":()V
  #23 = Utf8               symjava/bytecode/BytecodeFunc
  #24 = Class              #23            //  symjava/bytecode/BytecodeFunc
{
  public double apply(double[]);
    flags: ACC_PUBLIC
    Code:
      stack=4, locals=5, args_size=2
         0: dconst_0      
         1: dstore_2      //sum=0
         2: iconst_0      
         3: istore        4 //i=0
         5: goto          18
         8: aload_1       
         9: iload         4
        11: daload        //args[i]
        12: dload_2       //sum
        13: dadd          //args[i]+sum
        14: dstore_2      //sum = args[i]+sum
        15: iinc          4, 1 //i++
        18: iload         4 
        20: bipush        10
        22: if_icmplt     8 //i<10
        25: dload_2       
        26: dreturn       //return sum
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
               0      27     0  this   Lsymjava/bytecode/TestForLoop;
               0      27     1  args   [D
               1      26     2   sum   D
               3      24     4     i   I

  public symjava.bytecode.TestForLoop();
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0       
         1: invokespecial #22                 // Method java/lang/Object."<init>":()V
         4: return        
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
               0       5     0  this   Lsymjava/bytecode/TestForLoop;
}
*/