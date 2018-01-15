package de.fh.search;

public abstract class Goal {
	
	// Hunter's risk tolerance for this goal
	// A bigger number means more risk
	protected float riskTolerance;
	
	protected Goal(final float risk) {
		this.riskTolerance = risk;
	}
	
	public float getRiskTolerance() {
		return this.riskTolerance;
	}

	/*
	 * Check if Node is the goal for the search
	 */
	abstract protected boolean isNodeGoal(final Node node);
}