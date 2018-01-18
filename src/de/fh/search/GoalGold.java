package de.fh.search;

import de.fh.TileType;


/*
 * TODO: Untested
 * 
 * This gloal looks for a unknown tile which is close to the hunter  
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
		System.out.println("Goal check: " + node);
		if(node.getTile().getTileType() == TileType.UNKNOWN 
				&& node.getValue().getRisk() <= this.riskTolerance) {
			System.out.println(" -> is goal!");
			return true;
		}
		System.out.println(" -> is NOT goal!");
		return false;
	}
}
