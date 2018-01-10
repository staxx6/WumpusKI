package de.fh.search;

import de.fh.TileType;


/*
 * TODO: Untested
 */
public class GoalUnknown extends Goal {

	public GoalUnknown(final float risk) {
		super(risk);
	}
	
	// No need to look for "e" empty tiles
	// A new tile will be creat if node/search picks one up
	@Override
	public boolean isNodeGoal(final Node node) {
		if(node.getTile().getTileType() == TileType.UNKNOWN 
				&& node.getValue() <= this.risk) {
			return true;
		}
		return false;
	}
}
