package de.fh.search;


/*
 * This class calculate the value for a node
 * TODO: Untested
 * TODO: Nothing done here // have to do more?
 */
public class NodeValue {
	
	private float pathCost;
	private float risk;
	
	public NodeValue() {
		this.pathCost = 1;
		this.risk = 0;
	}
	
	public NodeValue(final float pathCost, final float risk) {
		this.pathCost = pathCost;
		this.risk = risk;
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
	
	public float getRisk() {
		return risk;
	}
	
	/*
	 * Returns f(x) = g(x) + h(x)
	 */
	public float get() {
		return pathCost + risk;
	}
}
