package io.lambdacloud.symjava.examples.fem;

public class Node {
	int index;
	int type = 0;

	public double[] coords;
	
	public Node(double ...coords) {
		this.coords = coords;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	
	public int getDim() {
		return coords.length;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("N");
		sb.append(index);
		sb.append("[");
		sb.append(type);
		sb.append("]=(");
		for(double d : coords) {
			sb.append(d);
			sb.append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append(")");
		return sb.toString();
	}
}