package io.lambdacloud.symjava.examples.fem;

import java.util.ArrayList;
import java.util.List;

import io.lambdacloud.symjava.domains.Domain;
import io.lambdacloud.symjava.domains.Domain1D;
import static io.lambdacloud.symjava.symbolic.Symbol.x;

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
