package symbolic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import symbolic.Add;
import symbolic.Divide;
import symbolic.Expr;
import symbolic.Multiply;
import symbolic.Negate;
import symbolic.Reciprocal;
import symbolic.Subtract;
import symbolic.Symbol;
import symbolic.Symbols;

public class Utils {

//	public static class Tuple4<T> {
//		public T o1, o2, o3, o4;
//		public Tuple4(T o1, T o2, T o3, T o4) {
//			this.o1 = o1;
//			this.o2 = o2;
//			this.o3 = o3;
//			this.o4 = o4;
//		}
//	}
//	public static <T> List<Tuple4<T>> C_4_2(T o1, T o2, T o3, T o4) {
//		List<Tuple4<T>> list = new ArrayList<Tuple4<T>>();
//		list.add(new Tuple4<T>(o1, o2, o3, o4));
//		list.add(new Tuple4<T>(o1, o3, o2, o4));
//		list.add(new Tuple4<T>(o1, o4, o2, o3));
//		//list.add(new Tuple4<T>(o2, o3, o1, o4));
//		//list.add(new Tuple4<T>(o2, o4, o1, o3));
//		//list.add(new Tuple4<T>(o3, o4, o1, o2));
//		return list;
//	}
	
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
				if(mulList.size() > 2) {
					simplifyMultiplyListHelper(mulList);
				}
				if(sign == -1) {
					rlt.add(new Negate(multiplyListToExpr(mulList)));
				} else {
					rlt.add(multiplyListToExpr(mulList));
				}				
			}
		}
		if(addList.size() > 2) {
			simplifyAddListHelper(rlt);
		}
		return addListWithNegateToExpr(rlt);
	}
	
	
//	public static List<Expr> simplifyAddList(Expr expr1, Expr expr2) {
//		List<Expr> l1 = flattenAddAndSort(expr1);
//		List<Expr> l2 = flattenAddAndSort(expr2);
//		if(l1.size() == 1 && l2.size() == 1) {
//			l1.addAll(l2);
//			sortExprList(l1);
//			return l1;
//		}
//		l1.addAll(l2);
//		return simplifyAddListHelper(l1);
//	}
//
//	public static List<Expr> simplifyAddList(Expr expr) {
//		return simplifyAddListHelper(flattenAddAndSort(expr));
//	}
//	
//	/**
//	 * expr1 - expr2
//	 * e.g. (x+y+z) - (x+y)
//	 * @param expr1
//	 * @param expr2
//	 * @return
//	 */
//	public static List<Expr> simplifySubtractList(Expr expr1, Expr expr2) {
//		List<Expr> l1 = flattenAddAndSort(expr1);
//		List<Expr> l2 = flattenAddAndSort(expr2);
//		if(l1.size() == 1 && l2.size() == 1) {
//			l1.add(new Negate(l2.get(0)));
//			sortExprList(l1);
//			return l1;
//		}
//		l1.addAll(l2);		
//		return simplifyAddListHelper(l1);
//	}
	
//	protected static List<Expr> simplifyAddListHelper(List<Expr> l1, List<Expr> l2) {
//		if(l1.size() == 1 && l2.size() == 1) {
//			l1.addAll(l2);
//			sortExprList(l1);
//			return l1;
//		}
//		List<Expr> l3 = new ArrayList<Expr>();
//		Iterator<Expr> it1 = l1.iterator();
//		while(it1.hasNext()) {
//			Expr e1 = it1.next();
//			Iterator<Expr> it2 = l2.iterator();
//			while(it2.hasNext()) {
//				Expr e2 = it2.next();
//				Expr simIns = Add.simplifiedIns(e1, e2);
//				if( simIns.getSimplifyOps() > e1.getSimplifyOps() + e2.getSimplifyOps() ) {
//					l3.add(simIns);
//					it2.remove();
//					it1.remove();
//					break;
//				}
//			}
//		}
//		l1.addAll(l2);
//		sortExprList(l1);
//		if(l3.size() > 0) {
//			return simplifyAddListHelper(l3, l1);
//		}
//		return l1;
//	}
	
	protected static List<Expr> simplifyAddListHelper(List<Expr> l) {
		List<Expr> l2 = new ArrayList<Expr>();
		
		while(true) {
			boolean foundPair = false;
			for(int i=0; i<l.size(); i++) {
				boolean found = false;
				for(int j=i+1; j<l.size(); j++) {
					Expr simIns = Add.shallowSimplifiedIns(l.get(i), l.get(j));
					if( simIns.getSimplifyOps() > l.get(i).getSimplifyOps() + l.get(j).getSimplifyOps() ) {
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
	
//	public static List<Expr> simplifyMultiplyList(Expr expr1, Expr expr2) {
//		List<Expr> l1 = flattenMultiplyAndSort(expr1);
//		List<Expr> l2 = flattenMultiplyAndSort(expr2);
//		if(l1.size() == 1 && l2.size() == 1) {
//			l1.addAll(l2);
//			sortExprList(l1);
//			return l1;
//		}
//		l1.addAll(l2);
//		return simplifyMultiplyListHelper(l1);
//	}
//	
//	public static List<Expr> simplifyMultiplyList(Expr expr) {
//		return simplifyMultiplyListHelper(flattenMultiplyAndSort(expr));
//	}	
	
	protected static List<Expr> simplifyMultiplyListHelper(List<Expr> l) {
		List<Expr> l2 = new ArrayList<Expr>();
		
		while(true) {
			boolean foundPair = false;
			for(int i=0; i<l.size(); i++) {
				boolean found = false;
				for(int j=i+1; j<l.size(); j++) {
					Expr simIns = Multiply.shallowSimplifiedIns(l.get(i), l.get(j));
					if( simIns.getSimplifyOps() > l.get(i).getSimplifyOps() + l.get(j).getSimplifyOps() ) {
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
	
//	public static Expr addListToExpr(List<Expr> list) {
//		if(list.size() == 1)
//			return list.get(0);
//		else {
//			Expr rlt = list.get(0);
//			for(int i=1; i<list.size(); i++) {
//				rlt = new Add(rlt, list.get(i));
//			}
//			return rlt;
//		}
//	}
	
	public static Expr addListWithNegateToExpr(List<Expr> list) {
		if(list.size() == 1)
			return list.get(0);
		else {
			Expr rlt = list.get(0);
			for(int i=1; i<list.size(); i++) {
				Expr e = list.get(i);
				if(e instanceof Negate) {
					Negate ee = (Negate)e;
					rlt = Subtract.shallowSimplifiedIns(rlt, ee.base);
				} else
					rlt = Add.shallowSimplifiedIns(rlt, e);
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
					rlt = Divide.shallowSimplifiedIns(rlt, ee.base);
				} else {
					rlt = Multiply.shallowSimplifiedIns(rlt, list.get(i));
				}
			}
			return rlt;
		}		
	}
}
