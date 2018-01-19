package de.fh.search;

import de.fh.TileType;
import de.fh.util.Vector2;

public class GoalLocation extends Goal {
	
	private Vector2 pos;
	
	public GoalLocation(final Vector2 pos, final float riskTolerance) {
		super(riskTolerance);
		this.pos = pos;
	}
	
	/*
	 * TODO: stupid search?
	 */
	@Override
	public boolean isNodeGoal(final Node node) {
//		System.out.println("Check: " + node.getTile().getPosVector() + " == " + this.pos);
		if(node.getTile().getPosVector().equals(this.pos)) {
//			System.out.println("true!");
			return true;
		} else {
			if(node.getTile().getPossibleTypes().contains(TileType.WALL)) {
				return true;
			}
//			System.out.println("false!");
		}
		return false;
	}
	
	public Vector2 getLocation() {
		return this.pos;
	}
}
