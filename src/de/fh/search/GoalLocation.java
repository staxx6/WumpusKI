package de.fh.search;

import de.fh.TileType;
import de.fh.util.Vector2;

public class GoalLocation extends Goal {
	
	private Vector2 pos;
	private boolean checkWallsExtra;
	
	public GoalLocation(final Vector2 pos, final float riskTolerance, final boolean checkWallsExtra) {
		super(riskTolerance);
		this.pos = pos;
		this.checkWallsExtra = checkWallsExtra;
		
		this.useRisk = true;
		this.usePathCost = true;
		this.useDistanceCost = true;
	}
	
	/*
	 * TODO: stupid search?
	 */
	@Override
	public boolean isNodeGoal(final Node node) {
		if(node.getTile().getPosVector().equals(this.pos)) {
			return true;
		} else if(this.checkWallsExtra) {
			if(node.getTile().getPossibleTypes().contains(TileType.WALL)) {
				return true;
			}
		}
		return false;
	}
	
	public Vector2 getLocation() {
		return this.pos;
	}
}
