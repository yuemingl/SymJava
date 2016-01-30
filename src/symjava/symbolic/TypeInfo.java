package symjava.symbolic;

import symjava.symbolic.Expr.TYPE;

/**
 * Provide type information of a symbol
 * type: see enum TYPE
 * dim: dimension of the type (for tensor, matrix and vector)
 * 
 */
public class TypeInfo {
	public TYPE type;
	public int[] dim;
	
	public static TypeInfo tiBoolean = new TypeInfo(TYPE.BOOLEAN);
	public static TypeInfo tiByte = new TypeInfo(TYPE.BYTE);
	public static TypeInfo tiChar = new TypeInfo(TYPE.CHAR);
	public static TypeInfo tiShort = new TypeInfo(TYPE.SHORT);
	public static TypeInfo tiInt = new TypeInfo(TYPE.INT);
	public static TypeInfo tiLong = new TypeInfo(TYPE.LONG);
	public static TypeInfo tiFloat = new TypeInfo(TYPE.FLOAT);
	public static TypeInfo tiDouble = new TypeInfo(TYPE.DOUBLE);
	
	public TypeInfo(TYPE type) {
		this.type = type;
		dim = new int[0];
	}
	public TypeInfo(TYPE type, int[] dim) {
		this.type = type;
		this.dim = dim;
	}
}
