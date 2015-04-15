package symjava.symbolic.utils;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.bytecode.BytecodeVecFunc;
import symjava.bytecode.IR;
import symjava.bytecode.VecFuncs;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Symbol;

public class JIT {
	
	public static BytecodeFunc compile(Expr[] args, Expr expr) {
		if(expr instanceof Func) {
			Func func = (Func)expr;
			return func.toBytecodeFunc();
		} else {
			Func func = new Func("JITFunc_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), expr);
			func.args = args;
			return func.toBytecodeFunc(true, false);
		}
	}
	
	public static BytecodeFunc compile(Expr expr) {
		if(expr instanceof Func) {
			Func func = (Func)expr;
			return func.toBytecodeFunc();
		} else {
			Func func = new Func("JITFunc_"+java.util.UUID.randomUUID().toString().replaceAll("-", ""), expr);
			return func.toBytecodeFunc(true, false);
		}
	}
	
	public static BytecodeVecFunc compile(Expr[] args, Expr[] exprs) {
		boolean isWriteFile = true;
		boolean staticMethod = false;
		try {
			int NMaxExpr = 36;
			FuncClassLoader<BytecodeVecFunc> fcl = new FuncClassLoader<BytecodeVecFunc>();
			List<Expr> nonZeroList = new ArrayList<Expr>();
			List<Integer> nonZeroIdx = new ArrayList<Integer>();
			for(int i=0; i<exprs.length; i++) {
				if(!Utils.symCompare(Symbol.C0, exprs[i])) {
					nonZeroList.add(exprs[i]);
					nonZeroIdx.add(i);
				}
			}
			if(exprs.length > NMaxExpr) {
				int N = exprs.length;
				List<Expr> batchExprs = new ArrayList<Expr>();
				List<Integer> batchOutPos = new ArrayList<Integer>();
				VecFuncs ret = new VecFuncs();
				for(int i = 0; i<nonZeroList.size(); i++) {
					batchExprs.add(nonZeroList.get(i));
					batchOutPos.add(nonZeroIdx.get(i));
					if(i%NMaxExpr == NMaxExpr-1) {
						String className = "JITVecFunc_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX__"+i+"___outof___"+exprs.length+"___XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
						ClassGen genClass = BytecodeUtils.genClassBytecodeVecFunc(className, batchExprs, batchOutPos, args, 
								isWriteFile, staticMethod);
						BytecodeVecFunc func = fcl.newInstance(genClass);
						ret.addFunc(func, batchOutPos.get(0));
						batchExprs.clear();
						batchOutPos.clear();
					}
				}
				int remain = N%NMaxExpr;
				if(remain > 0) {
					String className = "JITVecFunc_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX__"+(N-remain)+"___outof___"+exprs.length+"___XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
					ClassGen genClass = BytecodeUtils.genClassBytecodeVecFunc(className, batchExprs, batchOutPos, args, 
							isWriteFile, staticMethod);
					BytecodeVecFunc func = fcl.newInstance(genClass);
					ret.addFunc(func, batchOutPos.get(0));
				}
				return ret;
			} else {
				String className = "JITVecFunc_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX__"+exprs.length+"___XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
				ClassGen genClass = BytecodeUtils.genClassBytecodeVecFunc(className, nonZeroList, nonZeroIdx, args, 
						isWriteFile, staticMethod);
				return fcl.newInstance(genClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static BytecodeBatchFunc compileBatchFunc(Expr[] args, Expr expr) {
		String className = "JITVecFunc_YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		ClassGen genClass = BytecodeUtils.genClassBytecodeBatchFunc(className,expr, args, true, false);
		FuncClassLoader<BytecodeBatchFunc> fcl = new FuncClassLoader<BytecodeBatchFunc>();
		return fcl.newInstance(genClass);
	}
	
	public static IR getIR(Expr[] args, Expr expr) {
		String className = "JITVecFunc_YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"+java.util.UUID.randomUUID().toString().replaceAll("-", "");
		ClassGen genClass = BytecodeUtils.genClassBytecodeBatchFunc(className,expr, args, true, false);
		IR ir =  new IR();
		ir.type = 1;
		ir.name = genClass.getJavaClass().getClassName();
		ir.bytes = genClass.getJavaClass().getBytes();
		return ir;
		
	}
	
	public static void main(String[] args) {
//		Expr[] exprs = new Expr[3];
//		exprs[0] = Symbol.x;
//		exprs[1] = Symbol.x * Symbol.y;
//		exprs[2] = Symbol.y + 1;
//		BytecodeVecFunc vecFunc = compile(new Expr[]{Symbol.x, Symbol.y}, exprs);
//		double[] outAry = new double[3];
//		vecFunc.apply(outAry, 0, 10.0,20.0);
//		for(double d : outAry)
//			System.out.println(d);
		
		Expr expr = Symbol.x + Symbol.y;
		BytecodeBatchFunc vecFunc = JIT.compileBatchFunc(new Expr[]{Symbol.x, Symbol.y}, expr);
		double[] outAry = new double[3];
		double[][] params = {
				{1.0,  2.0, 3.0},
				{10.0, 20.0, 30.0}
		};
		vecFunc.apply(outAry, 0, params);
		for(double d : outAry)
			System.out.println(d);
		
	}
}
