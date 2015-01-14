package symbolic;

import static com.sun.org.apache.bcel.internal.Constants.ACC_PUBLIC;
import static com.sun.org.apache.bcel.internal.Constants.ACC_SUPER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Utils {
	public static void post_order(Expr e, List<Expr> outList) {
		if(e instanceof BinaryOp) {
			BinaryOp be = (BinaryOp)e; 
			post_order(be.left, outList);
			post_order(be.right, outList);
		} else if(e instanceof Reciprocal) {
			Reciprocal ue = (Reciprocal)e;
			outList.add(Symbol.C1);
			post_order(ue.base, outList);
		} else if(e instanceof UnaryOp) {
			UnaryOp ue = (UnaryOp)e; 
			post_order(ue.base, outList);
		} else if(e instanceof Summation) {
			Summation se = (Summation)e;
			for(int i=se.start; i<se.end; i++)
				post_order(se.getSummand(i), outList);
		}
		outList.add(e);
	}
	
	public static Symbol[] extractSymbols(Expr expr) {
		Set<Symbol> set = new HashSet<Symbol>();
		List<Expr> list = new ArrayList<Expr>();
		post_order(expr, list);
		for(Expr e : list) {
			if(e instanceof Symbol) {
				set.add((Symbol)e);
			}
		}
		Symbol[] rlt = new Symbol[set.size()];
		int idx = 0;
		for(Symbol s : set) {
			rlt[idx++] = s;
		}
		Arrays.sort(rlt, new Comparator<Symbol>() {
			@Override
			public int compare(Symbol o1, Symbol o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return rlt;
	}
	
	public static String joinName(Expr[] exprs, String deliminator) {
		if(exprs.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<exprs.length; i++) {
			sb.append(exprs[i].toString());
			sb.append(deliminator);
		}
		return sb.substring(0, sb.length()-deliminator.length());
	}
	
	public static void genClass(Func fun) {
		String packageName = "bytecode";
		String clsName = fun.name;
		String fullClsName = packageName+"."+clsName;
		ClassGen cg = new ClassGen(fullClsName, "java.lang.Object",
				"<generated>", ACC_PUBLIC | ACC_SUPER, new String[]{"bytecode.BytecodeFunc"});
		ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);
		
		MethodGen mg = new MethodGen(ACC_PUBLIC, // access flags
				Type.DOUBLE, // return type
				new Type[] { // argument types
					new ArrayType(Type.DOUBLE, 1) 
				}, 
				new String[] { "args" }, // arg names
				"apply", fullClsName, // method, class
				il, cp);
		
		Symbol[] fArgs = fun.args;
		HashMap<Symbol, Integer> argsMap = new HashMap<Symbol, Integer>();
		for(int i=0; i<fArgs.length; i++) {
			argsMap.put(fArgs[i], i);
		}
		List<Expr> insList = new ArrayList<Expr>();
		post_order(fun.expr, insList);

		for(int i=0; i<insList.size(); i++) {
			Expr ins = insList.get(i);
			if(ins instanceof Symbol) {
				Symbol s = (Symbol)ins;
				int argIdx = argsMap.get(s);
				il.append(new ALOAD(1));
				il.append(new PUSH(cp, argIdx));
				il.append(new DALOAD());
			} else if(ins instanceof SymReal<?>) {
				Number s = (Number)((SymReal<?>)ins).getVal();
				il.append(new PUSH(cp, s.doubleValue()));
			} else if(ins instanceof Add) {
				il.append(new DADD());
			} else if(ins instanceof Subtract) {
				il.append(new DSUB());
			} else if(ins instanceof Multiply) {
				il.append(new DMUL());
			} else if(ins instanceof Divide) {
				il.append(new DDIV());
			} else if(ins instanceof Power) {
				Power p = (Power)ins;
				il.append(new PUSH(cp, (double)p.exponent));
				il.append(factory.createInvoke("java.lang.Math", "pow",
						Type.DOUBLE, new Type[] { Type.DOUBLE, Type.DOUBLE }, Constants.INVOKESTATIC));				
			} else if(ins instanceof Reciprocal) {
				il.append(new DDIV());
			} else if(ins instanceof Negate) {
				il.append(new PUSH(cp, -1.0));		
				il.append(new DMUL());				
			} else {
				throw new RuntimeException(ins.getClass() + " is unknown!");
			}
		}
		//il.append(new ALOAD(1));
		//il.append(new ARRAYLENGTH());
		//il.append(new I2D());

		il.append(InstructionConstants.DRETURN);
		
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		il.dispose(); // Allow instruction handles to be reused
		
		cg.addEmptyConstructor(ACC_PUBLIC);
		try {
			cg.getJavaClass().dump("bin/bytecode/"+clsName+".class");
		} catch (java.io.IOException e) {
			System.err.println(e);
		}			
	}
	
	public static class Tuple4<T> {
		public T o1, o2, o3, o4;
		public Tuple4(T o1, T o2, T o3, T o4) {
			this.o1 = o1;
			this.o2 = o2;
			this.o3 = o3;
			this.o4 = o4;
		}
	}
	public static <T> List<Tuple4<T>> C_4_2(T o1, T o2, T o3, T o4) {
		List<Tuple4<T>> list = new ArrayList<Tuple4<T>>();
		list.add(new Tuple4<T>(o1, o2, o3, o4));
		list.add(new Tuple4<T>(o1, o3, o2, o4));
		list.add(new Tuple4<T>(o1, o4, o2, o3));
		//list.add(new Tuple4<T>(o2, o3, o1, o4));
		//list.add(new Tuple4<T>(o2, o4, o1, o3));
		//list.add(new Tuple4<T>(o3, o4, o1, o2));
		return list;
	}
	
	public static List<Expr> flattenAddAndSort(Expr expr) {
		List<Expr> l = new LinkedList<Expr>();
		expr.flattenAdd(l);
		sortExprList(l);
		return l;
	}

	public static List<Expr> flattenMultiplyAndSort(Expr expr) {
		List<Expr> l = new LinkedList<Expr>();
		expr.flattenMultiply(l);
		sortExprList(l);
		return l;
	}
	
	public static List<Expr> sortExprList(List<Expr> list) {
		Collections.sort(list, new Comparator<Expr>() {
			@Override
			public int compare(Expr o1, Expr o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return list;
	}

	public static boolean flattenSortAndCompare(Expr expr1, Expr expr2) {
		List<Expr> l1 = flattenAddAndSort(expr1);
		List<Expr> l2 = flattenAddAndSort(expr2);
		if(l1.size() != l2.size())
			return false;

		for(int i=0; i<l1.size(); i++) {
			Expr e1 = l1.get(i);
			Expr e2 = l2.get(i);
			List<Expr> le1 = flattenMultiplyAndSort(e1);
			List<Expr> le2 = flattenMultiplyAndSort(e2);
			if(le1.size() != le2.size())
				return false;

			for(int j=0; j<le1.size(); j++) {
				if(!symCompare(le1.get(j), le2.get(j)))
					return false;
			}
		}
		return true;
	}
	
	public static boolean symCompare(Expr expr1, Expr expr2) {
		if( expr1 instanceof Symbol || 
			expr2 instanceof Symbol || 
			expr1 instanceof Symbols || 
			expr2 instanceof Symbols) {
			return expr1 == expr2;
		}
		return expr1.symEquals(expr2);
	}
	
	public static List<Expr> simplifyAddList(Expr expr1, Expr expr2) {
		List<Expr> l1 = flattenAddAndSort(expr1);
		List<Expr> l2 = flattenAddAndSort(expr2);
		return simplifyAddListHelper(l1, l2);
	}
	
	protected static List<Expr> simplifyAddListHelper(List<Expr> l1, List<Expr> l2) {
		if(l1.size() == 1 && l2.size() == 1) {
			l1.addAll(l2);
			sortExprList(l1);
			return l1;
		}
		List<Expr> l3 = new LinkedList<Expr>();
		Iterator<Expr> it1 = l1.iterator();
		while(it1.hasNext()) {
			Expr e1 = it1.next();
			Iterator<Expr> it2 = l2.iterator();
			while(it2.hasNext()) {
				Expr e2 = it2.next();
				Expr simIns = Add.simplifiedIns(e1, e2);
				if( simIns.getSimplifyOps() > e1.getSimplifyOps() + e2.getSimplifyOps() ) {
					l3.add(simIns);
					it2.remove();
					it1.remove();
					break;
				}
			}
		}
		l1.addAll(l2);
		sortExprList(l1);
		if(l3.size() > 0) {
			return simplifyAddListHelper(l3, l1);
		}
		return l1;
	}
}
