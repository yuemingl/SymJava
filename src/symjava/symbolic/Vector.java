package symjava.symbolic;

import symjava.matrix.SymVector;

public class Vector extends Tensor {

	public int nStart;
	public int nDim;

	public Vector(String name, int nDim) {
		super(name);
		this.nStart = 0;
		this.nDim = nDim;
	}

	public Vector(String name, int nStart, int nDim) {
		super(name);
		this.label = name+"("+nStart+","+nDim+")";
		this.nStart = nStart;
		this.nDim = nDim; 
	}
	
	public SymVector split(int nBlock) {
		int n = nDim/nBlock;
		if(nDim%nBlock > 0)
			n = (nDim+(nBlock-nDim%nBlock))/nBlock;
		int last_n = nDim%n==0?n:nDim%n;
		System.out.println(n);
		System.out.println(last_n);
		Expr[] items = new Expr[nBlock];
		for(int j=0; j<nBlock-1; j++) {
			items[j] = new Vector(this.label, j*n, n);
		}
		items[nBlock-1] = new Vector(this.label, (nBlock-1)*n, last_n);
		return new SymVector(items);
	}


	public static void main(String[] args) {
		Vector v = new Vector("A",8);
		SymVector sv = v.split(3);
		System.out.println(sv);
	}
}
