package symjava.symbolic;

import symjava.symbolic.Expr.TYPE;

/**
 * Provide type information of a symbol
 * type: see enum TYPE
 * dim: dimension of the type (for tensor, matrix and vector)
 * 
 * @author yuemingliu
 *
 */
public class TypeInfo {
	public TYPE type;
	public int[] dim;
}
