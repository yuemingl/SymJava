package symjava.symbolic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import symjava.symbolic.Add;
import symjava.symbolic.Divide;
import symjava.symbolic.Expr;
import symjava.symbolic.Multiply;
import symjava.symbolic.Negate;
import symjava.symbolic.Reciprocal;
import symjava.symbolic.Subtract;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;

public class Utils {

	public static List<Expr> flattenAddAndSort(Expr expr) {
		List<Expr> l = new ArrayList<Expr>();
		expr.flattenAdd(l);
		sortExprList(l);
		return l;
	}

	public static List<Expr> flattenMultiplyAndSort(Expr expr) {
		List<Expr> l = new ArrayList<Expr>();
		expr.flattenMultiply(l);
		sortExprList(l);
		return l;
	}
	
	public static List<Expr> sortExprList(List<Expr> list) {
		Collections.sort(list, new Comparator<Expr>() {
			@Override
			public int compare(Expr o1, Expr o2) {
				return o1.getSortKey().compareTo(o2.getSortKey());
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
	
	public static int getMultiplyGlobalSign(List<Expr> list) {
		int count = 0;
		for(Expr e : list) {
			if(e instanceof Negate) {
				count++;
			}
		}
		if(count%2==1)
			return -1;
		return 1;
	}
	
	public static List<Expr> removeNegate(List<Expr> list) {
		List<Expr> rlt = new ArrayList<Expr>();
		for(Expr e : list) {
			if(e instanceof Negate) {
				Negate ee = (Negate)e;
				rlt.add(ee.base);
			} else {
				rlt.add(e);
			}
		}
		list.clear();
		list.addAll(rlt);
		return list;
	}
	
	public static Expr flattenSortAndSimplify(Expr expr) {
		List<Expr> addList = flattenAddAndSort(expr);
		List<Expr> rlt = new ArrayList<Expr>();
		for(int i=0; i<addList.size(); i++) {
			Expr e = addList.get(i);
			List<Expr> mulList = flattenMultiplyAndSort(e);
			if(mulList.size() == 1)
				rlt.addAll(mulList);
			else {
				int sign = getMultiplyGlobalSign(mulList);
				removeNegate(mulList);
				//if(mulList.size() > 2) {
					simplifyMultiplyListHelper(mulList);
				//}
				if(sign == -1) {
					rlt.add(new Negate(multiplyListToExpr(mulList)));
				} else {
					rlt.add(multiplyListToExpr(mulList));
				}				
			}
		}
		//if(addList.size() > 2) {
			simplifyAddListHelper(rlt);
		//}
		Expr ret = addListToExpr(rlt);
		ret.setAsSimplified();
		return ret;
	}
	
	protected static List<Expr> simplifyAddListHelper(List<Expr> l) {
		List<Expr> l2 = new ArrayList<Expr>();
		
		while(true) {
			boolean foundPair = false;
			for(int i=0; i<l.size(); i++) {
				boolean found = false;
				for(int j=i+1; j<l.size(); j++) {
					int oldSimOps = l.get(i).getSimplifyOps() + l.get(j).getSimplifyOps();
					//Expr simIns = Add.simplifiedIns(l.get(i), l.get(j));
					Expr simIns = Add.shallowSimplifiedIns(l.get(i), l.get(j));
					if( simIns.getSimplifyOps() > oldSimOps ) {
						l2.add(simIns);
						found = true;
						foundPair = true;
						l.remove(j);
						break;
					}
				}
				if(found) {
					l.remove(i);
					break;
				}
			}
			if(!foundPair)
				break;
		}
		
		if(l2.size() > 0) {
			l.addAll(l2);
			return simplifyAddListHelper(l);
		}
		
		sortExprList(l);
		return l;
	}
	
	protected static List<Expr> simplifyMultiplyListHelper(List<Expr> l) {
		List<Expr> l2 = new ArrayList<Expr>();
		
		while(true) {
			boolean foundPair = false;
			for(int i=0; i<l.size(); i++) {
				boolean found = false;
				for(int j=i+1; j<l.size(); j++) {
					int oldSimOps = l.get(i).getSimplifyOps() + l.get(j).getSimplifyOps();
					Expr simIns = Multiply.shallowSimplifiedIns(l.get(i), l.get(j));
					if( simIns.getSimplifyOps() > oldSimOps ) {
						l2.add(simIns);
						found = true;
						foundPair = true;
						l.remove(j);
						break;
					}
				}
				if(found) {
					l.remove(i);
					break;
				}
			}
			if(!foundPair)
				break;
		}
		
		if(l2.size() > 0) {
			l.addAll(l2);
			return simplifyMultiplyListHelper(l);
		}
		
		sortExprList(l);
		return l;
	}
	
	public static Expr addListToExpr(List<Expr> list) {
		if(list.size() == 1)
			return list.get(0);
		else {
			Expr rlt = list.get(0);
			for(int i=1; i<list.size(); i++) {
				Expr e = list.get(i);
				if(e instanceof Negate) {
					Negate ee = (Negate)e;
					//rlt = Subtract.shallowSimplifiedIns(rlt, ee.base);
					rlt = new Subtract(rlt, ee.base);
				} else
					//rlt = Add.shallowSimplifiedIns(rlt, e);
					rlt = new Add(rlt, e);
			}
			return rlt;
		}
	}	
	
	public static Expr multiplyListToExpr(List<Expr> list) {
		if(list.size() == 1)
			return list.get(0);
		else {
			Expr rlt = list.get(0);
			for(int i=1; i<list.size(); i++) {
				Expr e = list.get(i);
				if(e instanceof Reciprocal) {
					Reciprocal ee = (Reciprocal)e;
					//rlt = Divide.shallowSimplifiedIns(rlt, ee.base);
					rlt = new Divide(rlt, ee.base);
				} else {
					//rlt = Multiply.shallowSimplifiedIns(rlt, e);
					rlt = new Multiply(rlt, e);
				}
			}
			return rlt;
		}		
	}
	
	public static List<Symbol> extractSymbols(Expr ...exprs) {
		Set<Symbol> set = new HashSet<Symbol>();
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<exprs.length; i++) {
			BytecodeUtils.post_order(exprs[i], list);
			for(Expr e : list) {
				if(e instanceof Symbol) {
					set.add((Symbol)e);
				}
			}
		}
		List<Symbol> rlt = new ArrayList<Symbol>();
		rlt.addAll(set);
		Collections.sort(rlt, new Comparator<Symbol>() {
			@Override
			public int compare(Symbol o1, Symbol o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return rlt;
	}
	
	public static boolean containSymbol(Expr expr, Symbol s) {
		List<Expr> list = new ArrayList<Expr>();
		BytecodeUtils.post_order(expr, list);
		for(Expr e : list) {
			if(e instanceof Symbol) {
				if(e.symEquals(s))
					return true;
			}
		}
		return false;
	}
}
