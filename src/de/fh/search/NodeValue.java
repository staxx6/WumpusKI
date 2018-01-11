package de.fh.search;


/*
 * This class calculate the value for a node
 * TODO: Untested
 * TODO: Nothing done here
 */
public class NodeValue {
	
	private float pathCost;
	private float risk;
	
	public NodeValue() {
	}
	
	public void setPathCost(final float pathCost) {
		this.pathCost = pathCost;
	}
	
	public float getPathCost() {
		return this.pathCost;
	}
	
	public void setRisk(final float risk) {
		this.risk = risk;
	}
	
	/*
	 * Returns f(x) = g(x) + h(x)
	 * 
	 */
	public float get() {
		return pathCost + risk;
	}
}
