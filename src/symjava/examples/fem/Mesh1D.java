package symjava.examples.fem;

import java.util.ArrayList;
import java.util.List;

import symjava.domains.Domain;
import symjava.domains.Domain1D;
import static symjava.symbolic.Symbol.x;

public class Mesh1D extends Domain1D {
	public List<Node> nodes = new ArrayList<Node>();
	public List<Domain> eles = new ArrayList<Domain>();
	
	public Mesh1D(String name) {
		super(name, x);
	}
	
	public List<Domain> getSubDomains() {
		return eles;
	}	
}
