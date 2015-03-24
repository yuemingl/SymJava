package symjava.domains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	protected Expr constraint;
	
	public static class CoordVarInfo {
		Double stepSize;
		Expr minBound;
		Expr maxBound;
	}
	
	protected Map<Expr, CoordVarInfo> infoMap = new HashMap<Expr, CoordVarInfo>();
	
	
	/**
	 * Return a (n-1) dim domain that represents the boundary of the domain
	 * The parameter specifies the conditions for which part of the boundary will be returned
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
	 * Split the domain evenly with size step in the given direction
	 * @param step
	 * @return
	 */
	public Domain setStepSize(Expr x, double stepSize) {
		CoordVarInfo info = infoMap.get(x);
		if(info == null) {
			info = new CoordVarInfo();
			infoMap.put(x, info);
		}
		info.stepSize = stepSize;
		return this;
	}
	
	/**
	 * Get the step size of the given direction
	 * @param x
	 * @return
	 */
	public Double getStepSize(Expr x) {
		CoordVarInfo info = infoMap.get(x);
		if(info == null)
			return null;
		return info.stepSize;
	}
	
	public Domain setStepSize(double stepSize) {
		for(Expr x : this.coordVars) {
			setStepSize(x, stepSize);
		}
		return this;
	}
	
	public Double getStepSize() {
		for(Expr x : this.coordVars) {
			return getStepSize(x);
		}
		return null;
	}
	
	/**
	 * Set the lower bound and upper bound for coordinate variable x
	 * 
	 * @param x
	 * @param minBound
	 * @param maxBound
	 * @return
	 */
	public Domain setBound(Expr x, Expr minBound, Expr maxBound) {
		CoordVarInfo info = infoMap.get(x);
		if(info == null) {
			info = new CoordVarInfo();
			infoMap.put(x, info);
		}
		info.minBound = minBound;
		info.maxBound = maxBound;
		return this;
	}
	public Domain setBound(Expr x, double minBound, Expr maxBound) {
		setBound(x, Expr.valueOf(minBound), maxBound);
		return this;
	}
	public Domain setBound(Expr x, Expr minBound, double maxBound) {
		setBound(x, minBound, Expr.valueOf(maxBound));
		return this;
	}
	public Domain setBound(Expr x, double minBound, double maxBound) {
		setBound(x, Expr.valueOf(minBound), Expr.valueOf(maxBound));
		return this;
	}
	
	/**
	 * Set a logic expression of equations to repreent the domain 
	 * For example, parameter logicalExpr:
	 * 
	 *  Expr logicalExpr = Ge.apply(x*x + y*y, 0.5) & Le.apply(x*x + y*y, 1.0)
	 *  //x*x + y*y >= 0.5 and x*x + y*y <= 1.0
	 *  
	 *  Define a domain object mydomain ...
	 *  mydomain.setConstraint(logicalExpr); 
	 *  //mydomain will be an annular shape
	 *  
	 * @param logicalExpr
	 * @return
	 */
	public Domain setConstraint(Expr logicalExpr) {
		this.constraint = logicalExpr;
		return this;
	}
	
	public Expr getConstraint() {
		return this.constraint;
	}
	
	public Expr getMinBound(Expr x) {
		CoordVarInfo info = infoMap.get(x);
		if(info == null)
			return null;
		return info.minBound;
	}
	
	public Expr getMaxBound(Expr x) {
		CoordVarInfo info = infoMap.get(x);
		if(info == null)
			return null;
		return info.maxBound;
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

