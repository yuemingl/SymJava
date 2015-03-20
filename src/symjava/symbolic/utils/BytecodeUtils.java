package symjava.symbolic.utils;

import static com.sun.org.apache.bcel.internal.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import symjava.bytecode.BytecodeFunc;
import symjava.domains.Interval;
import symjava.symbolic.Add;
import symjava.symbolic.Cos;
import symjava.symbolic.Divide;
import symjava.symbolic.Dot;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Infinity;
import symjava.symbolic.Integrate;
import symjava.symbolic.Log;
import symjava.symbolic.Multiply;
import symjava.symbolic.Negate;
import symjava.symbolic.Pow;
import symjava.symbolic.Reciprocal;
import symjava.symbolic.Sin;
import symjava.symbolic.Sqrt;
import symjava.symbolic.Subtract;
import symjava.symbolic.Sum;
import symjava.symbolic.SymReal;
import symjava.symbolic.Symbol;
import symjava.symbolic.SymConst;
import symjava.symbolic.Tan;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.arity.UnaryOp;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DADD;
import com.sun.org.apache.bcel.internal.generic.DALOAD;
import com.sun.org.apache.bcel.internal.generic.DDIV;
import com.sun.org.apache.bcel.internal.generic.DMUL;
import com.sun.org.apache.bcel.internal.generic.DSUB;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.POP;
import com.sun.org.apache.bcel.internal.generic.POP2;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

public class BytecodeUtils {
	public static void post_order(Expr e, List<Expr> outList) {
		if(e == null) return;
		if(e instanceof BinaryOp) {
			BinaryOp be = (BinaryOp)e; 
			post_order(be.arg1, outList);
			post_order(be.arg2, outList);
		} else if(e instanceof Reciprocal) {
			Reciprocal ue = (Reciprocal)e;
			outList.add(Symbol.C1);
			post_order(ue.arg, outList);
		} else if(e instanceof UnaryOp) {
			UnaryOp ue = (UnaryOp)e; 
			post_order(ue.arg, outList);
		} else if(e instanceof Sum) {
			Sum se = (Sum)e;
			for(int i=se.start; i<=se.end; i++)
				post_order(se.getSummand(i), outList);
		} else if(e instanceof Dot) {
			Dot de = (Dot)e;
			post_order(de.getExpr(), outList);
			return;
		} else if(e instanceof Func) {
			Func f = (Func)e;
			if(!f.isAbstract()) {
				post_order(f.getExpr(), outList);
				return;
			}
		} else if(e instanceof Integrate) {
			Integrate INT = (Integrate)e;
			if(INT.domain instanceof Interval) {
				Interval I = (Interval)INT.domain;
				post_order(I.getStart(), outList);
				post_order(I.getEnd(), outList);
				//Integrand will not be added to the outList since we don't want the dummy variable to be exposed
				//outList.add(new Func("integrand"+java.util.UUID.randomUUID().toString().replaceAll("-", ""),INT.integrand));
			} else {
				//TODO
				//Support multi-variable integrate
			}
		}
		outList.add(e);
	}
	
	public static Expr[] extractArguments(Func func) {
		Set<Expr> set = new HashSet<Expr>();
		List<Expr> list = new ArrayList<Expr>();
		post_order(func, list);
		for(Expr e : list) {
			if(e instanceof Symbol) {
				set.add(e);
			} else if(e instanceof Func) {
				Func fe = (Func)e;
				for(Expr arg : fe.args) {
					if(arg instanceof Symbol)
						set.add((Symbol)arg);
				}
			}
		}
		Expr[] rlt = new Expr[set.size()];
		int idx = 0;
		for(Expr s : set) {
			rlt[idx++] = s;
		}
		Arrays.sort(rlt, new Comparator<Expr>() {
			@Override
			public int compare(Expr o1, Expr o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return rlt;
	}
	
	public static ClassGen genClass(Func fun, boolean writeClassFile, boolean staticMethod) {
		String packageName = "symjava.bytecode";
		String clsName = fun.getName();
		String fullClsName = packageName+"."+clsName;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"symjava.bytecode.BytecodeFunc"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);
		
		short acc_flags = ACC_PUBLIC;
		if(staticMethod)
			acc_flags |= ACC_STATIC;
		MethodGen mg = new MethodGen(acc_flags, // access flags
				Type.DOUBLE, // return type
				new Type[] { // argument types
					new ArrayType(Type.DOUBLE, 1) 
				}, 
				new String[] { "args" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		
		Expr[] fExprArgs = extractArguments(fun);
		if(fExprArgs.length > fun.args.length) {
			System.out.println(
				String.format("Warning: Arguments of %s is different from it's expression:\n>>>Defined args:%s \n>>>Express args:%s",
						fun.getName(),
						Utils.joinLabels(fun.args, ","),
						Utils.joinLabels(fExprArgs, ",")));
			Set<Expr> argSet = new HashSet<Expr>();
			for(Expr e : fun.args)
				argSet.add(e);
			for(Expr e : fExprArgs)
				argSet.add(e);
			Expr[] allArgs = argSet.toArray(new Expr[0]);
			fExprArgs = Utils.sortExprs(allArgs);
			System.out.println(
					String.format(">>>Using args: %s", Utils.joinLabels(fExprArgs, ","))
					);
		} else {
			fExprArgs = fun.args;
		}
		HashMap<Expr, Integer> argsMap = new HashMap<Expr, Integer>();
		for(int i=0; i<fExprArgs.length; i++) {
			argsMap.put(fExprArgs[i], i);
		}
		List<Expr> insList = new ArrayList<Expr>();
		post_order(fun.getExpr(), insList);
		if(insList.size() == 0) {
			throw new RuntimeException(String.format("Function %s is an empty function. Nothing to generate!",
					fun.getName()));
		}

		for(int i=0; i<insList.size(); i++) {
			Expr ins = insList.get(i);
			if(ins instanceof Symbol) {
				Integer argIdx = argsMap.get(ins);
				if(argIdx == null) {
					throw new IllegalArgumentException(ins+" is not in the argument list of "+fun.getLabel());
				}
				if(staticMethod)
					il.append(new ALOAD(0)); //for static method
				else
					il.append(new ALOAD(1));
				il.append(new PUSH(cp, argIdx));
				il.append(new DALOAD());
			} else if(ins instanceof SymReal<?>) {
				Number s = (Number)((SymReal<?>)ins).getValue();
				il.append(new PUSH(cp, s.doubleValue()));
			} else if(ins instanceof SymConst) {
				il.append(new PUSH(cp, ((SymConst)ins).getValue()));
			} else if(ins instanceof Add) {
				il.append(new DADD());
			} else if(ins instanceof Subtract) {
				il.append(new DSUB());
			} else if(ins instanceof Multiply) {
				il.append(new DMUL());
			} else if(ins instanceof Divide) {
				il.append(new DDIV());
			} else if(ins instanceof Pow) {
				Pow p = (Pow)ins;
				if(p.arg2 instanceof SymReal<?>) {
					SymReal<?> realExp = (SymReal<?>)p.arg2;
					if(realExp.isInteger()) {
						il.append(new POP2()); //Replace double value to integer
						il.append(new PUSH(cp, realExp.getIntValue()));
						il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "powi",
								Type.DOUBLE, new Type[] { Type.DOUBLE, Type.INT }, Constants.INVOKESTATIC));
						continue;
					}
				}
				il.append(factory.createInvoke("java.lang.Math", "pow",
						Type.DOUBLE, new Type[] { Type.DOUBLE, Type.DOUBLE }, Constants.INVOKESTATIC));
			} else if(ins instanceof Sqrt) {
				Sqrt p = (Sqrt)ins;
				if(p.arg2 instanceof SymReal<?>) {
					SymReal<?> realRoot = (SymReal<?>)p.arg2;
					if(realRoot.getIntValue() == 2) {
						il.append(new POP2());
						il.append(factory.createInvoke("java.lang.Math", "sqrt",
								Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
						continue;
					}
				}
				il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "sqrt",
						Type.DOUBLE, new Type[] { Type.DOUBLE, Type.DOUBLE }, Constants.INVOKESTATIC));
			} else if(ins instanceof Sin) {
				il.append(factory.createInvoke("java.lang.Math", "sin",
						Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
			} else if(ins instanceof Cos) {
				il.append(factory.createInvoke("java.lang.Math", "cos",
						Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
			} else if(ins instanceof Tan) {
				il.append(factory.createInvoke("java.lang.Math", "tan",
						Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
//			} else if(ins instanceof Log10) {
//				il.append(factory.createInvoke("java.lang.Math", "log10",
//						Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
//				il.append(new POP2()); //this pop out the result of log10, not the base
			} else if(ins instanceof Log) {
//				if(Utils.symCompare(((Log) ins).arg1, Exp.E)) {
//					il.append(factory.createInvoke("java.lang.Math", "log",
//							Type.DOUBLE, new Type[] { Type.DOUBLE }, Constants.INVOKESTATIC));
//					il.append(new POP2());
//				} else {
					il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "log",
							Type.DOUBLE, new Type[] { Type.DOUBLE,  Type.DOUBLE }, Constants.INVOKESTATIC));
//				}
			} else if(ins instanceof Reciprocal) {
				il.append(new DDIV());
			} else if(ins instanceof Negate) {
				il.append(new PUSH(cp, -1.0));
				il.append(new DMUL());
			} else if(ins instanceof Infinity) {
				throw new RuntimeException(ins.getClass() + "Infinity cannot be used in numerical computation, use a proper number instead!");
			} else if(ins instanceof Integrate) {
				Integrate INT = (Integrate)ins;
				if(INT.domain instanceof Interval) {
					//Compile the integrand
					Func f = new Func("integrand_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""),INT.integrand);
					//System.out.println(f);
					f.toBytecodeFunc(true, true); //Load class, could be better method to load a class
					
					//TODO read this: http://stackoverflow.com/questions/19119702/injecting-code-in-an-existing-method-using-bcel/19219759#19219759
					if(INT.domain.getStep() == null) {
						throw new RuntimeException("Please specifiy the step size for you integral: "+INT);
					}
					
					il.append(new PUSH(cp, INT.domain.getStep()));
					il.append(new PUSH(cp, f.getName()));
					il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "numIntegrate1D",
							Type.DOUBLE, new Type[] { Type.DOUBLE, Type.DOUBLE, Type.DOUBLE, Type.STRING }, Constants.INVOKESTATIC));
				} else {
					//TODO
					throw new RuntimeException("Unsupported Integrate: "+INT);
				}
			} else {
				throw new RuntimeException(ins.getClass() + " is not supported in this version when generating bytecode function!");
			}
		}

		il.append(InstructionConstants.DRETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		if(writeClassFile) {
			try {
				cg.getJavaClass().dump("bin/symjava/bytecode/"+clsName+".class");
			} catch (java.io.IOException e) {
				System.err.println(e);
			}
		}
		return cg;
	}
}
