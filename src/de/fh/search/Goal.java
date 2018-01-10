package de.fh.search;

public abstract class Goal {
	
	// Hunter's risk tolerance for this goal
	// A bigger number means more risk
	protected float risk;
	
	protected Goal(final float risk) {
		this.risk = risk;
	}

	/*
	 * Check if Node is the goal for the search
	 */
	abstract protected boolean isNodeGoal(final Node node);
}