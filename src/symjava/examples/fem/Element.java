package symjava.examples.fem;

import java.util.ArrayList;
import java.util.List;

import symjava.domains.Domain;
import symjava.math.Transformation;
import symjava.symbolic.Expr;


public class Element extends Domain {
	public List<Node> nodes = new ArrayList<Node>();
	
	int index;
	int dim;

	public Element(String label, Expr ...coordVars) {
		this.label = label;
		this.coordVars = coordVars;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getLocalIndex(Node node) {
		for(int i=0; i<nodes.size(); i++) {
			if(nodes.get(i) == node)
				return i;
		}
		return -1;
	}
	
	public Element setNodes(Node ...nodes) {
		for(Node n : nodes) {
			this.nodes.add(n);
		}
		this.dim = nodes[0].getDim();
		return this;
	}
	
	@Override
	public Domain transform(String label, Transformation trans) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDim() {
		return dim;
	}
	
	public double[] getNodeCoords() {
		double[] rlt = new double[nodes.size()*dim];
		int index = 0;
		for(int j=0; j<dim; j++) {
			for(Node n : nodes) {
				rlt[index++] = n.coords[j];
			}
		}
		return rlt;
	}

}