package de.fh;

import de.fh.util.Vector2;

/*
 * A helper class to move the hunter
 * TODO: Delete class? - need only to move one tile
 */
public class MoveHelper {
	
	private Vector2 goToPos;
	private Vector2 currPos;
//	private Direction currDirection; // Hunter
	
//	public MoveHelper(final Direction hunterDirection, final Vector2 hunterPos) {
//		this.currDirection = hunterDirection;
//		this.currPos = hunterPos;
//		this.goToPos = new Vector2(0, 0);
//	}
	
	public void setGoToPos(final Vector2 pos) {
		this.goToPos = pos;
	}
	
	public void setCurrPos(final Vector2 pos) {
		this.currPos = currPos;
	}
	
	public void updateMove() {
		/*
		 * If y > < dann bewege N S and if E W
		 * direct way! Search gives only   
		 */
	}
}
