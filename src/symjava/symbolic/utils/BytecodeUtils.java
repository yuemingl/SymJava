package symjava.symbolic.utils;

import static com.sun.org.apache.bcel.internal.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import symjava.domains.Domain2D;
import symjava.domains.Interval;
import symjava.logic.And;
import symjava.logic.Logic;
import symjava.logic.Not;
import symjava.logic.Or;
import symjava.logic.Xor;
import symjava.math.Dot;
import symjava.relational.Eq;
import symjava.relational.Ge;
import symjava.relational.Gt;
import symjava.relational.Le;
import symjava.relational.Lt;
import symjava.relational.Neq;
import symjava.relational.Relation;
import symjava.symbolic.Add;
import symjava.symbolic.Cos;
import symjava.symbolic.Divide;
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
import symjava.symbolic.arity.NaryOp;
import symjava.symbolic.arity.TernaryOp;
import symjava.symbolic.arity.UnaryOp;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.AASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DADD;
import com.sun.org.apache.bcel.internal.generic.DALOAD;
import com.sun.org.apache.bcel.internal.generic.DASTORE;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.org.apache.bcel.internal.generic.DDIV;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DMUL;
import com.sun.org.apache.bcel.internal.generic.DSUB;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.I2D;
import com.sun.org.apache.bcel.internal.generic.IAND;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IOR;
import com.sun.org.apache.bcel.internal.generic.IXOR;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.POP2;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.SASTORE;
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
			} else if(INT.domain instanceof Domain2D) {
				if(INT.isMultipleIntegral()) {
					Expr[] coord = INT.domain.getCoordVars();
					Expr lastVar = coord[coord.length-1];
					post_order(INT.domain.getMinBound(lastVar), outList);
					post_order(INT.domain.getMaxBound(lastVar), outList);
				} else {
					//Monte Carlo integration on 2D domain
					
				}
			}else {
				//Support multiple integration on 3D or higher dim domains
				
			}
		} else if(e instanceof NaryOp || e instanceof TernaryOp) {
			Expr[] args = e.args();
			for(int i=0; i<args.length; i++)
				post_order(args[i], outList);
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
	
	public static ClassGen genClassBytecodeFunc(Func fun, boolean writeClassFile, boolean staticMethod) {
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
		System.out.println(fun.getLabel()+": "+fun.getExpr());
		//System.out.println(Utils.joinLabels(fExprArgs, ","));
		
		HashMap<Expr, Integer> argsMap = new HashMap<Expr, Integer>();
		for(int i=0; i<fExprArgs.length; i++) {
			argsMap.put(fExprArgs[i], i);
		}
		int argsIndex = 1;
		if(staticMethod)
			argsIndex = 0;
		addToInstructionList(mg, cp, factory, il, argsIndex, fun.getExpr(), fun.args, argsMap);

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
	
	public static ClassGen genClassBytecodeVecFunc(String className, Expr[] exprs, Expr[] args, boolean writeClassFile, boolean staticMethod) {
		String packageName = "symjava.bytecode";
		String fullClsName = packageName+"."+className;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"symjava.bytecode.BytecodeVecFunc"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);
		
		short acc_flags = ACC_PUBLIC;
		if(staticMethod)
			acc_flags |= ACC_STATIC;
		MethodGen mg = new MethodGen(acc_flags, // access flags
				Type.VOID, // return type
				new Type[] { // argument types
					new ArrayType(Type.DOUBLE, 1),
					new ArrayType(Type.DOUBLE, 1) 
				},
				new String[] { "outAry", "args" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		
		HashMap<Expr, Integer> argsMap = new HashMap<Expr, Integer>();
		for(int i=0; i<args.length; i++) {
			argsMap.put(args[i], i);
		}

//		LocalVariableGen lg;
//		lg = mg.addLocalVariable("retArray",
//				new ArrayType(Type.DOUBLE, 1), null, null);
//		int retArray = lg.getIndex();
//		il.append(InstructionConstants.ACONST_NULL);
//		il.append(new PUSH(cp, exprs.length));
//		il.append(new NEWARRAY(Type.DOUBLE));
//		il.append(new ASTORE(retArray));
		for(int i=0; i<exprs.length; i++) {
			if(!Utils.symCompare(Symbol.C0, exprs[i])) {
				il.append(new ALOAD(1));
				il.append(new PUSH(cp,i));
					addToInstructionList(mg, cp, factory, il, 2, exprs[i], args, argsMap);
				il.append(new DASTORE());
			}
		}
//		il.append(new ALOAD(retArray));
//		il.append(InstructionConstants.ARETURN);
		il.append(InstructionConstants.RETURN);
		
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		if(writeClassFile) {
			try {
				cg.getJavaClass().dump("bin/symjava/bytecode/"+className+".class");
			} catch (java.io.IOException e) {
				System.err.println(e);
			}
		}
		return cg;
	}
	
	public static void addToInstructionList(MethodGen mg, ConstantPoolGen cp, InstructionFactory factory, InstructionList il, 
			int argsIndex, Expr expr, Expr[] args, HashMap<Expr, Integer> argsMap) {
		//Traverse the expression tree
		List<Expr> insList = new ArrayList<Expr>();
		post_order(expr, insList);
		if(insList.size() == 0) {
			throw new RuntimeException("Expressionis empty. Nothing to generate!");
		}

		for(int insIndex=0; insIndex<insList.size(); insIndex++) {
			Expr ins = insList.get(insIndex);
			if(ins instanceof Symbol) {
				Integer argIdx = argsMap.get(ins);
				if(argIdx == null) {
					throw new IllegalArgumentException(ins+" is not in the argument list of "+expr.getLabel());
				}
				//0 for static method
				//1 for BytecodeFunc
				//2 for BytecodeVecFunc
				il.append(new ALOAD(argsIndex)); 
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
				//Reorder args for integrand
				Expr[] integrandArgs = new Expr[args.length + INT.domain.getDim()];
				int count = 0;
				for(Expr ee : INT.domain.getCoordVars())
					integrandArgs[count++] = ee;
				for(Expr ee : args) 
					integrandArgs[count++] = ee;
				//Compile the integrand
				Func integrand = new Func("integrand_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""),INT.integrand, integrandArgs);
				//System.out.println(f);
				integrand.toBytecodeFunc(true, true); //Load class, could be better method to load a class

				if(INT.domain instanceof Interval) {
					//TODO read this: http://stackoverflow.com/questions/19119702/injecting-code-in-an-existing-method-using-bcel/19219759#19219759
					if(INT.domain.getStepSize() == null) {
						throw new RuntimeException("Please specifiy the step size for you integral: "+INT);
					}
					//We have begin,end parameters on the top of the VM stack
					il.append(new PUSH(cp, INT.domain.getStepSize()));
					il.append(new PUSH(cp, integrand.getName()));
					il.append((new ALOAD(1))); //additional parameters from user's call
					il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "numIntegrate1D",
							Type.DOUBLE, new Type[] { 
							Type.DOUBLE, Type.DOUBLE, Type.DOUBLE, 
							Type.STRING, 
							new ArrayType(Type.DOUBLE, 1) 
							}, Constants.INVOKESTATIC));
				} else if(INT.domain instanceof Domain2D && INT.isMultipleIntegral()) {
					Expr[] coord = INT.domain.getCoordVars();
					Expr x = coord[0];
					Expr y = coord[1];
					Expr xMin = INT.domain.getMinBound(x);
					Expr xMax = INT.domain.getMaxBound(x);
					//Expr yMin = INT.domain.getMinBound(y);
					//Expr yMax = INT.domain.getMaxBound(y);
					Func fxMin = new Func("integrate_bound_"+x+"Min_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), xMin);
					Func fxMax = new Func("integrate_bound_"+x+"Max_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), xMax);
					//Func fyMin = new Func("integrate_bound_"+y+"Min_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), yMin);
					//Func fyMax = new Func("integrate_bound_"+y+"Maz_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), yMax);
					//System.out.println("integrand="+f);
					//System.out.println("fxMin="+fxMin);
					//System.out.println("fxMax="+fxMax);
					fxMin.toBytecodeFunc(true, true);
					fxMax.toBytecodeFunc(true, true);
					//fyMin.toBytecodeFunc(true, true);
					//fyMax.toBytecodeFunc(true, true);
					//We have begin,end parameters on the top of the VM stack
					if(INT.domain.getStepSize(x) == null) {
						throw new RuntimeException("Please specify step size for "+x);
					}
					if(INT.domain.getStepSize(y) == null) {
						throw new RuntimeException("Please specify step size for "+y);
					}
					il.append(new PUSH(cp, INT.domain.getStepSize(y)));
					il.append(new PUSH(cp, fxMin.getName()));
					il.append(new PUSH(cp, fxMax.getName()));
					il.append(new PUSH(cp, INT.domain.getStepSize(x)));
					il.append(new PUSH(cp, integrand.getName()));
					il.append((new ALOAD(1))); //additional parameters from user's call
					//Now the paramters are ready, call the function
					il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "numIntegrate2D",
							Type.DOUBLE, new Type[] { 
							Type.DOUBLE, Type.DOUBLE, Type.DOUBLE, 
							Type.STRING, Type.STRING, Type.DOUBLE, 
							Type.STRING,
							new ArrayType(Type.DOUBLE, 1)
					}, Constants.INVOKESTATIC));
				} else if(!INT.isMultipleIntegral()) {
					Expr[] coords = INT.domain.getCoordVars();
					LocalVariableGen lg;
					
					lg = mg.addLocalVariable("minBound",
						new ArrayType(Type.STRING, 1), null, null);
					int idxMinBound = lg.getIndex();
					il.append(InstructionConstants.ACONST_NULL);
					lg.setStart(il.append(new ASTORE(idxMinBound))); // "minBound" valid from here
					
					lg = mg.addLocalVariable("maxBound",
						new ArrayType(Type.STRING, 1), null, null);
					int idxMaxBound = lg.getIndex();
					il.append(InstructionConstants.ACONST_NULL);
					lg.setStart(il.append(new ASTORE(idxMaxBound))); // "maxBound" valid from here
					
					il.append(new PUSH(cp, coords.length));
					il.append(new ANEWARRAY(cp.addClass(Type.STRING)));
					il.append(new ASTORE(idxMinBound));
					
					il.append(new PUSH(cp, coords.length));
					il.append(new ANEWARRAY(cp.addClass(Type.STRING)));
					il.append(new ASTORE(idxMaxBound));
					
					for(int i=0; i<coords.length; i++) {
						Expr x = coords[i];
						Expr xMin = INT.domain.getMinBound(x);
						Expr xMax = INT.domain.getMaxBound(x);
						Func fxMin = new Func("integrate_bound_"+x+"Min_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), xMin);
						Func fxMax = new Func("integrate_bound_"+x+"Max_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), xMax);
						System.out.println("min bound="+fxMin);
						System.out.println("max bound="+fxMax);
						il.append(new ALOAD(idxMinBound));
						il.append(new PUSH(cp,i));
						il.append(new PUSH(cp, fxMin.getName()));
						il.append(new AASTORE());

						il.append(new ALOAD(idxMaxBound));
						il.append(new PUSH(cp,i));
						il.append(new PUSH(cp, fxMax.getName()));
						il.append(new AASTORE());

						fxMin.toBytecodeFunc(true, true);
						fxMax.toBytecodeFunc(true, true);
					}
					
					Func constr = new Func("constr_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), INT.domain.getConstraint(), integrandArgs);
					constr.toBytecodeFunc(true, true);
					
					il.append(new ALOAD(idxMinBound));
					il.append(new ALOAD(idxMaxBound));
					il.append(new PUSH(cp, integrand.getName()));
					il.append(new PUSH(cp, constr.getName()));
					il.append((new ALOAD(1))); //additional parameters from user's call
					//Now the paramters are ready, call the function
					il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeSupport", "numIntegrateMonteCarloND",
							Type.DOUBLE, new Type[] { 
							new ArrayType(Type.STRING,1),new ArrayType(Type.STRING,1), 
							Type.STRING,Type.STRING,
							new ArrayType(Type.DOUBLE, 1)
					}, Constants.INVOKESTATIC));
				} else {
					//TODO
					throw new RuntimeException("Unsupported Integrate: "+INT);
				}
			} else if(ins instanceof Gt) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFLE(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof Ge) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFLT(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof Lt) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFGE(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof Le) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFGT(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof Eq) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFNE(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof Neq) {
				il.append(new DCMPL());
				InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
				InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
				InstructionHandle nop = il.append(new NOP());
				il.insert(iconst1, new IFEQ(iconst0));
				il.insert(iconst0, new GOTO(nop));
			} else if(ins instanceof And) {
				il.append(new IAND());
			} else if(ins instanceof Or) {
				il.append(new IOR());
			} else if(ins instanceof Xor) {
				il.append(new IXOR());
			} else if(ins instanceof Not) {
				il.append(new PUSH(cp, 1));
				il.append(new IXOR());
			} else {
				throw new RuntimeException(ins.getClass() + " is not supported in this version when generating bytecode function!");
			}
		}
		if(expr instanceof Relation || expr instanceof Logic) {
			il.append(new I2D());
		}
	}
}