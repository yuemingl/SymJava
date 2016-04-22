package symjava.math;

import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.DADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.FADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.IADD;
import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.LADD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.Type;

import symjava.matrix.SymVector;
import symjava.symbolic.Add;
import symjava.symbolic.Expr;
import symjava.symbolic.SymReal;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.Vector;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.BytecodeUtils;
import symjava.symbolic.utils.Utils;

/**
 * Dot Product of two vectors
 *
 */
public class Dot extends BinaryOp {
	protected Expr expr = null;
	public Dot(SymVector l, SymVector r) {
		super(l,r);
		if(l.dim() != r.dim())
			throw new IllegalArgumentException("The size of the two vector must be the same!");
		arg1 = l;
		arg2 = r;
		if(arg1 instanceof Grad && arg2 instanceof Grad) {
			label = arg1 + " \\cdot " + arg2;
			sortKey = label;
			return;
		}
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<l.dim(); i++) {
			list.add(l.get(i).multiply(r.get(i)));
		}
		expr = Utils.addListToExpr(list).simplify();
		label = expr.toString();
		sortKey = label;
	}
	
	public Dot(Vector l, Vector r) {
		super(l,r);
		if(l.dim() != r.dim())
			throw new IllegalArgumentException("The size of the two vector must be the same!");
		arg1 = l;
		arg2 = r;
		label = "dot(" + arg1 + ", " + arg2 + ")";
		sortKey = label;
	}
	
	public static Expr apply(SymVector l, SymVector r) {
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<l.dim(); i++) {
			list.add(l.get(i).multiply(r.get(i)));
		}
		Expr ret = Utils.addListToExpr(list).simplify();
		if(ret instanceof SymReal<?>)
			return ret;
		Dot dot = new Dot(l, r);
		if(dot.expr != null)
			return dot.expr;
		return dot;
	}
	
	public static Expr apply(Vector l, Vector r) {
		return new Dot(l, r);
	}
	
	@Override
	public Expr diff(Expr expr) {
		if(this.expr == null) {
			Grad lg = (Grad)arg1;
			Grad rg = (Grad)arg2;
			if(lg.isAbstract() && rg.isAbstract()) {
				Expr d1 = Dot.apply(new Grad(lg.getFunc().diff(expr), lg.getFunc().args), rg);
				Expr d2 = Dot.apply(lg, new Grad(rg.getFunc().diff(expr), rg.getFunc().args));
				return Add.simplifiedIns(d1, d2);
			}
		}
		if(arg1 instanceof Grad && arg2 instanceof Grad) {
			Grad lg = (Grad)arg1;
			Grad rg = (Grad)arg2;
			Expr d1 = Dot.apply(lg.diff(expr), rg);
			Expr d2 = Dot.apply(lg, rg.diff(expr));
			return Add.simplifiedIns(d1, d2);
		}
		return this.expr.diff(expr);
	}

	@Override
	public Expr fdiff(Expr f, Expr df) {
		if(expr == null) {
			Grad lg = (Grad)arg1;
			Grad rg = (Grad)arg2;
			if(lg.isAbstract() && rg.isAbstract()) {
				Expr d1 = Dot.apply(new Grad(lg.getFunc().fdiff(f, df)), rg);
				Expr d2 = Dot.apply(lg, new Grad(rg.getFunc().fdiff(f, df)));
				return Add.shallowSimplifiedIns(d1, d2);
			}
		}
		return expr.fdiff(f, df);
	}
	
	@Override
	public Expr simplify() {
		if(expr == null)
			return this;
		return expr.simplify();
	}

	@Override
	public boolean symEquals(Expr other) {
		if(expr == null) {
			if(other instanceof Dot) {
				//TODO
			}
			return false;
		}
		return Utils.symCompare(expr, other);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		if(expr == null)
			outList.add(this);
		else
			expr.flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(expr == null)
			outList.add(this);
		else
			expr.flattenMultiply(outList);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		if(expr == null)
			//TODO How to deal with class Vector?
			return new Dot((SymVector)arg1.subs(from, to), (SymVector)arg2.subs(from, to));
		else
			return expr.subs(from, to);
	}
	
	public Expr getExpr() {
		if(this.expr != null)
			return this.expr;
		else {
			List<Expr> list = new ArrayList<Expr>();
			for(int i=0; i<arg1.dim(); i++) {
				list.add(arg1.get(i).multiply(arg2.get(i)));
			}
			return Utils.addListToExpr(list).simplify();
		}
	}
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		if(arg1.getType() == TYPE.VECTOR && arg2.getType() == TYPE.VECTOR) {
			arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
			il.append(factory.createInvoke("symjava.symbolic.utils.BytecodeOpSupport", "dot",
					//Type.DOUBLE, new Type[] { new ObjectType("Jama.Matrix"),new ObjectType("Jama.Matrix") },
					new ObjectType("Jama.Matrix"), new Type[] { new ObjectType("Jama.Matrix"),new ObjectType("Jama.Matrix") },
					Constants.INVOKESTATIC));
			return startPos;
		}
		//TODO
		return startPos;
	}

//	@Override
//	public TypeInfo getTypeInfo() {
//		return TypeInfo.tiDouble;
//	}
	
	@Override
	public void updateLabel() {
		// TODO Auto-generated method stub
		
	}
}
