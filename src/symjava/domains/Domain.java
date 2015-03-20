package symjava.domains;

import java.util.ArrayList;
import java.util.List;

import symjava.math.Transformation;
import symjava.numeric.NumFunc;
import symjava.symbolic.Expr;

/**
 * Class represents a domain in mathematics
 *
 */
public abstract class Domain {
	protected String label;
	protected Expr[] coordVars;
	Double step = null;
	
	/**
	 * Return a sub-domain that represents the boundary of the domain
	 * The parameter specify the conditions which part of the boundary will be returned
	 * @return
	 */
	public Domain getBoundary(NumFunc<?> func) {
		return null;
	}
	
	public void setCoordVars(Expr ...coordVars) {
		this.coordVars = coordVars;
	}
	
	/**
	 * Return the coordinate variables of the domain 
	 * @return
	 */
	public Expr[] getCoordVars() {
		return this.coordVars;
	}
	
	/**
	 * Do domain transformation according to the given <tt>trans</tt> object
	 * 
	 * @param label
	 * @param trans
	 * @return
	 */
	public abstract Domain transform(String label, Transformation trans);
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Return the label(name) of the domain
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Return the dimension of the domain
	 * @return
	 */
	public abstract int getDim();
	
	/**
	 * Return the integration points and weights in the following format:
	 * 
	 * x1,y1,z1,w1
	 * x2,y2,z2,w2
	 * ...
	 * xn,yn,zn,wn
	 * 
	 * @return
	 */
	public double[][] getIntWeightAndPoints(int order) {
		return null;
	}
	
	/**
	 * Split the domain evenly with size step in each direction
	 * @param step
	 * @return
	 */
	public Domain setStep(double step) {
		this.step = step;
		return this;
	}
	
	public Double getStep() {
		return step;
	}
	
	/**
	 * Return an array of sub-domains. If the domain has no sub-domains, return itself. 
	 * @return
	 */
	public List<Domain> getSubDomains() {
		List<Domain> rlt = new ArrayList<Domain>();
		rlt.add(this);
		return rlt;
	}
	
	public String toString() {
		return this.label;
	}
}
