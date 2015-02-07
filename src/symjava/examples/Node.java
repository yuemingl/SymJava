package symjava.examples;

public class Node {
	int index;
	double[] coords;
	int type;
	public Node(double ...coords) {
		this.coords = coords;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getDim() {
		return coords.length;
	}

}
