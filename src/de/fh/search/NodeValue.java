package de.fh.search;


/*
 * This class calculate the value for a node
 * TODO: Untested
 * TODO: Nothing done here
 */
public class NodeValue {
	
	private float pathCost;
	private float risk;
	
	public NodeValue(final int value) {
		this.value = value;
	}
	
	public float getValue() {
		return value;
	}
}
