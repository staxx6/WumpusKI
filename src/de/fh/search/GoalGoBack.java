package de.fh.search;

import de.fh.util.Vector2;

/*
 * This class is currently equal GoalLocation. Created this class
 * if there are new ideas which needs a new class.  
 */
public class GoalGoBack extends Goal {

	private Vector2 pos;
	
	public GoalGoBack(final Vector2 pos, final float riskTolerance) {
		super(riskTolerance);
		this.pos = pos;
		
		this.useRisk = true;
		this.usePathCost = true;
		this.useDistanceCost = true;
	}
	
	/*
	 * Should be only one tile away
	 */
	@Override
	public boolean isNodeGoal(final Node node) {
		if(node.getTile().getPosVector().equals(this.pos)) {
			return true;
		}
		return false;
	}
	
	public Vector2 getLocation() {
		return this.pos;
	}
}
