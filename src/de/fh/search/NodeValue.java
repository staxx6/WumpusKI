package de.fh.search;


/*
 * This class calculate the value for a node
 */
public class NodeValue {
	
	private float pathCost;
	private float distanceCost;
	private float risk;
	
	public NodeValue() {
		this.pathCost = 0;
		this.distanceCost = 0;
		this.risk = 0;
	}
	
	public NodeValue(final float risk,final float pathCost, final float distanceCost) {
		this.risk = risk;
		this.pathCost = pathCost;
		this.distanceCost = distanceCost;
	}
	
	public void setDistanceCost(final float distanceCost) {
		this.distanceCost = distanceCost;
	}
	
	public float getDIstanceCost() {
		return this.distanceCost;
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
	public float getAstar() {
		return risk + (distanceCost + pathCost);
	}
	
	public String toString() {
		return "NodeValue: risk: " + this.risk + " pathCost: " 
				+ pathCost + " distanceCost: " + this.distanceCost
					+ " aStar: " + (risk + (distanceCost + pathCost));
	}
}




