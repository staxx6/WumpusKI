package de.fh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fh.util.Vector2;

/*
 * TODO: Add a toString() methode
 */

/*
 * A Tile contain all the inforamtion about
 * position, type and possible types.   
 */
public class Tile {
	private TileType type;
	private Vector2 pos;
	private Set<TileType> possibleTypes;
	private boolean breeze = false; //TODO write down why it shouldn't be a TileType
	private Set<Integer> wumpusIds;
	
	// TODO heuristic stuff
	
	public Tile(final TileType type, final Vector2 pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public Tile(final TileType type, final int x, final int y) {
		this.type = type;
		this.pos = new Vector2(x, y);
	}
	
	/*
	 * Set a KNOWN type
	 * It clears the possible types 
	 */
	public void setTileType(final TileType type) {
		this.type = type;
		if(this.possibleTypes != null) this.possibleTypes.clear();
	}
	
	public TileType getTileType() {
		return this.type;
	}
	
	public void setPos(final int x, final int y) {
		this.pos.set(x, y);
	}
	
	public void setPosX(final int x) {
		this.pos.setX(x);
	}
	
	public void setPosY(final int y) {
		this.pos.setY(y);
	}
	
	public Vector2 getPosVector() {
		return this.pos;
	}
	
	public int getPosX() {
		return this.pos.getX();
	}
	
	public int getPosY() {
		return this.pos.getY();
	}

	public Set<TileType> getPossibleTypes() {
		return possibleTypes;
	}

	public void setPossibleTypes(Set<TileType> possibleTypes) {
		if(this.type == TileType.UNKNOWN)
			this.possibleTypes = possibleTypes;
		else
			System.out.println("Don't need to add possible types, type should be clear!");
	}
	
	public void addPossibleType(final TileType type) {
		if(this.possibleTypes == null) this.possibleTypes = new HashSet<>();
		if(this.type == TileType.UNKNOWN) 
			this.possibleTypes.add(type);
	}
	
	//TODO: Untested
	public void removePossibleType(final TileType type) {
		if(this.possibleTypes == null) return;
		if(!this.possibleTypes.remove(type)) {
			System.out.println("DEBUG: Couldn't remove possible tile type " + type 
					+ " from tile: " + toString());
		}; // TODO Error if not there? looks it returns a bool
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(final boolean breeze) {
		this.breeze = breeze;
	}

	/*
	 * @return Could be a null return = no wumpus
	 */
	public Set<Integer> getWumpusIds() {
		return wumpusIds;
	}

	public void addWumpusId(final int id) {
		if(this.wumpusIds == null) this.wumpusIds = new HashSet<>();
		this.wumpusIds.add(id);
	}
	
	//TODO Possible error if empty?
	public void removeWumpusId(final int id) {
		this.wumpusIds.remove(id);
	}
	
	public void removeAllWumpusIds() {
		if(this.wumpusIds != null)
			this.wumpusIds.clear();
	}
}










