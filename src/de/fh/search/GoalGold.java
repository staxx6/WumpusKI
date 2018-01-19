package de.fh.search;

import de.fh.TileType;


/*
 * This gloal looks for a unknown tile which is close to the hunter  
 * -> It dosn't look directly for gold!
 */
public class GoalGold extends Goal {

	public GoalGold(final float riskTolerance) {
		super(riskTolerance);
	}
	
	/*
	 *  No need to look for "e" empty tiles
	 *  A new tile will be creat if node/search picks one up
	*/
	@Override
	public boolean isNodeGoal(final Node node) {
		if(node.getTile().getTileType() == TileType.UNKNOWN 
				&& node.getValue().getRisk() <= this.riskTolerance) {
			return true;
		}
		return false;
	}
}
