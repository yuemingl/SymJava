package symjava.examples.fem;

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

}
