package symjava.symbolic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lambdacloud.core.CloudSD;
import symjava.symbolic.Add;
import symjava.symbolic.Divide;
import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.Func;
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
		sortExprs(l);
		return l;
	}

	public static List<Expr> flattenMultiplyAndSort(Expr expr) {
		List<Expr> l = new ArrayList<Expr>();
		expr.flattenMultiply(l);
		sortExprs(l);
		return l;
	}
	
	public static Expr[] sortExprs(Expr[] exprs) {
		Arrays.sort(exprs, new Comparator<Expr>() {
			@Override
			public int compare(Expr o1, Expr o2) {
				if(o1 instanceof Symbol && o2 instanceof Symbol) {
					Symbol s1 = (Symbol)o1;
					Symbol s2 = (Symbol)o2;
					
					int rlt = s1.getPrefix().compareTo(s2.getPrefix());
					if(rlt == 0) {
						if(s1.containsSubIndex() && s2.containsSubIndex())
							return s1.getSubIndex()-s2.getSubIndex();
					}
					return rlt;
				}
				return o1.getSortKey().compareTo(o2.getSortKey());
			}
		});
		return exprs;
	}

	public static List<Expr> sortExprs(List<Expr> list) {
		Collections.sort(list, new Comparator<Expr>() {
			@Override
			public int compare(Expr o1, Expr o2) {
				if(o1 instanceof Symbol && o2 instanceof Symbol) {
					Symbol s1 = (Symbol)o1;
					Symbol s2 = (Symbol)o2;
					
					int rlt = s1.getPrefix().compareTo(s2.getPrefix());
					if(rlt == 0) {
						if(s1.containsSubIndex() && s2.containsSubIndex())
							return s1.getSubIndex()-s2.getSubIndex();
					}
					return rlt;
				}
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
	
	/**
	 * Return true if expr1 is equal expr2
	 * @param expr1
	 * @param expr2
	 * @return
	 */
	public static boolean symCompare(Expr expr1, Expr expr2) {
		if( expr1 instanceof Symbol || expr1 instanceof Symbols)
			return expr1.symEquals(expr2);
		if( expr2 instanceof Symbol || expr2 instanceof Symbols)
			return expr2.symEquals(expr1);
		return expr1.symEquals(expr2);
	}
	
	public static Boolean symCompareNull(Expr expr1, Expr expr2) {
		if( (expr1 == null && expr2 != null) || (expr1 != null && expr2 == null) )
			return false;
		else if(expr1 == null || expr2 == null)
			return null;
		else 
			return symCompare(expr1, expr2);
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
				rlt.add(ee.arg);
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
		
		sortExprs(l);
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
		
		sortExprs(l);
		return l;
	}
	
	public static Expr addListToExpr(List<Expr> list) {
		if(list.size() <= 1) {
			if(list.size() == 0)
				throw new RuntimeException("Empty expression list!");
			return list.get(0);
		} else {
			Expr rlt = list.get(0);
			for(int i=1; i<list.size(); i++) {
				Expr e = list.get(i);
				if(e instanceof Negate) {
					Negate ee = (Negate)e;
					//rlt = Subtract.shallowSimplifiedIns(rlt, ee.base);
					rlt = new Subtract(rlt, ee.arg);
				} else
					//rlt = Add.shallowSimplifiedIns(rlt, e);
					rlt = new Add(rlt, e);
			}
			return rlt;
		}
	}
	
	public static Expr multiplyListToExpr(List<Expr> list) {
		if(list.size() <= 1) {
			if(list.size() == 0)
				throw new RuntimeException("Empty expression list!");
			return list.get(0);
		} else {
			Expr rlt = list.get(0);
			for(int i=1; i<list.size(); i++) {
				Expr e = list.get(i);
				if(e instanceof Reciprocal) {
					Reciprocal ee = (Reciprocal)e;
					//rlt = Divide.shallowSimplifiedIns(rlt, ee.base);
					rlt = new Divide(rlt, ee.arg);
				} else {
					//rlt = Multiply.shallowSimplifiedIns(rlt, e);
					rlt = new Multiply(rlt, e);
				}
			}
			return rlt;
		}		
	}
	
//	public static void extractSymbols(Expr expr, Set<Expr> set) {
//		for(Expr e : expr.args())
//			extractSymbols(e, set);
//		set.add(expr);
//	}
	
	public static List<Expr> extractSymbols(Expr ...exprs) {
		Set<Expr> set = new HashSet<Expr>();
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<exprs.length; i++) {
			BytecodeUtils.post_order(exprs[i], list);
			for(Expr e : list) {
				if(e instanceof Symbol) {
					set.add((Symbol)e);
				} else if(e instanceof Symbols) {
					set.add((Symbols)e);
				} else if(e instanceof Func) {
					Func fe = (Func)e;
					//Parameters in a function
					for(Expr arg : fe.args) {
						if(arg instanceof Symbol)
							set.add((Symbol)arg);
					}
				}
			}
		}
		List<Expr> rlt = new ArrayList<Expr>();
		rlt.addAll(set);
		sortExprs(rlt);
		return rlt;
	}
	
	public static List<CloudSD> extractCloudVars(Expr ...exprs) {
		Set<Expr> set = new HashSet<Expr>();
		List<Expr> list = new ArrayList<Expr>();
		for(int i=0; i<exprs.length; i++) {
			BytecodeUtils.post_order(exprs[i], list);
			for(Expr e : list) {
				if(e instanceof CloudSD) {
					set.add((CloudSD)e);
				}
			}
		}
		List<Expr> rlt = new ArrayList<Expr>();
		rlt.addAll(set);
		sortExprs(rlt);
		List<CloudSD> rlt2 = new ArrayList<CloudSD>();
		for(Expr e : rlt) {
			rlt2.add((CloudSD)e);
		}
		return rlt2;
	}
	
	public static double[][] getDataFromCloudVars(CloudSD[] cloudVars) {
		double[][] rlt = new double[cloudVars.length][];
		for(int i=0; i<cloudVars.length; i++) {
			if(cloudVars[i].fetchToLocal())
				rlt[i] = cloudVars[i].getData();
		}
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
	
	public static List<String> toLabelList(List<Expr> list) {
		List<String> l = new ArrayList<String>();
		for(Expr e : list) {
			l.add(e.toString());
		}
		return l;
	}
	
	public static String joinLabels(Expr[] list, String deliminator) {
		StringBuilder sb = new StringBuilder();
		if(list == null) return null;
		for(Expr e : list) {
			sb.append(e.toString());
			sb.append(deliminator);
		}
		if(sb.length() > deliminator.length())
			sb.delete(sb.length()-deliminator.length(), sb.length());
		return sb.toString();
	}
	
	public static String joinLabels(String[] list, int startIdx, int endIdx, String deliminator) {
		StringBuilder sb = new StringBuilder();
		if(list == null) return null;
		for(int i=startIdx; i<endIdx; i++) {
			sb.append(list[i]);
			sb.append(deliminator);
		}
		if(sb.length() > deliminator.length())
			sb.delete(sb.length()-deliminator.length(), sb.length());
		return sb.toString();
	}

	public static String joinLabels(String[] list, String deliminator) {
		return joinLabels(list, 0, list.length, deliminator);
	}

	public static String joinLabels(List<Expr> list, String deliminator) {
		return joinLabels(list.toArray(new Expr[0]), deliminator);
	}
	
	public static Expr[] joinArrays(Expr[] ...arys) {
		int len = 0;
		for(int i=0; i<arys.length; i++) {
			len += arys[i].length;
		}
		Expr[] rlt = new Expr[len];
		int k = 0;
		for(int i=0; i<arys.length; i++) {
			for(Expr e : arys[i]) {
				rlt[k++] = e;
			}
		}
		return rlt;
	}
	
	public static TYPE getConvertedType(TYPE t1, TYPE t2) {
		if(t1 == t2) return t1;
		if(t1 == TYPE.BOOLEAN || t2 == TYPE.BOOLEAN) {
			if(t1 != TYPE.BOOLEAN || t2 != TYPE.BOOLEAN)
				throw new RuntimeException();
		}
		if(t1 == TYPE.VOID || t2 == TYPE.VOID) {
			if(t1 != TYPE.VOID || t2 != TYPE.VOID)
				throw new RuntimeException();
		}
		if(t1 == TYPE.DOUBLE || t2 == TYPE.DOUBLE) return TYPE.DOUBLE;
		if(t1 == TYPE.FLOAT || t2 == TYPE.FLOAT) return TYPE.FLOAT;
		if(t1 == TYPE.LONG || t2 == TYPE.LONG) return TYPE.LONG;
		if(t1 == TYPE.INT || t2 == TYPE.INT) return TYPE.INT;
		if(t1 == TYPE.SHORT || t2 == TYPE.SHORT) return TYPE.SHORT;
		if(t1 == TYPE.CHAR || t2 == TYPE.CHAR) return TYPE.CHAR;
		if(t1 != TYPE.BOOLEAN && t2 != TYPE.BYTE)
			throw new RuntimeException();
		return t1; //TYPE.BYTE
	}
}
