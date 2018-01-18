package de.fh;

import de.fh.game.Entity.Direction;
import de.fh.util.Vector2;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

/*
 * A helper class to move the hunter
 */
public class HunterMoveHelper {
	
	private Vector2 pos;
	private Direction dir;
	
	private boolean isTurning;
	private Direction nextDir;
	
	public HunterMoveHelper(final Vector2 hunterPos, final Direction hunterDir) {
		this.pos = hunterPos;
		this.dir = hunterDir;
		
		this.isTurning = false;
		this.nextDir = hunterDir;
	}
	
	public HunterAction moveTo(final Vector2 goToPos) {
		nextDir(goToPos);
		if(dir == nextDir) {
			return HunterAction.GO_FORWARD;
		} else {
			return turnToDir();
		}
	}
	
	private HunterAction turnToDir() {
		if(this.dir == Direction.NORTH) {
			if(this.nextDir == Direction.WEST) {
				this.dir = Direction.WEST;
				return HunterAction.TURN_LEFT;
			} else {
				this.dir = Direction.EAST;
				return HunterAction.TURN_RIGHT;
			}
		} else if(this.dir == Direction.EAST) {
			if(this.nextDir == Direction.NORTH) {
				this.dir = Direction.NORTH;
				return HunterAction.TURN_LEFT;
			} else {
				this.dir = Direction.SOUTH;
				return HunterAction.TURN_RIGHT;
			}
		} else if(this.dir == Direction.SOUTH) {
			if(this.nextDir == Direction.EAST) {
				this.dir = Direction.EAST;
				return HunterAction.TURN_LEFT;
			} else {
				this.dir = Direction.WEST;
				return HunterAction.TURN_RIGHT;
			}
		} else if(this.dir == Direction.WEST) {
			if(this.nextDir == Direction.SOUTH) {
				this.dir = Direction.SOUTH;
				return HunterAction.TURN_LEFT;
			} else {
				this.dir = Direction.NORTH;
				return HunterAction.TURN_RIGHT;
			}
		} else {
			throw new IllegalStateException("ERROR: No dir to move!");
		}
	}
	
	private void nextDir(final Vector2 goToPos) {
		if(this.pos.getX() > goToPos.getX()) {
			this.nextDir = Direction.WEST;
		} else if(this.pos.getX() < goToPos.getX()) {
			this.nextDir = Direction.EAST;
		}
		
		if(this.pos.getY() > goToPos.getY()) {
			this.nextDir = Direction.NORTH;
		} else if(this.pos.getY() < goToPos.getY()) {
			this.nextDir = Direction.SOUTH;
		}
	}
	
	public void updatePos() {
		if(this.isTurning) {
			// nothing
		} else {
			switch(this.dir) {
				 case NORTH:
	    			 this.pos.setY(this.pos.getY() - 1);
	    			 break;
	    		 case EAST:
	    			 this.pos.setX(this.pos.getX() + 1);
	    			 break;
	    		 case SOUTH:
	    			 this.pos.setY(this.pos.getY() + 1);
	    			 break;
	    		 case WEST:
	    			 this.pos.setX(this.pos.getX() - 1);
	    			 break;
	    		 default:
	    			 System.out.println("ERROR: Direction dosn't exist!");
	    			 break;
	    	}
		}
	}
	
	public Vector2 getCurrentPos() {
		return this.pos;
	}
	
	public Direction getCurrentDir() {
		return this.dir;
	}
}
