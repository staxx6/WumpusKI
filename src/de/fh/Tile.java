package de.fh;

import java.util.HashSet;
import java.util.Hashtable;
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
//	private Set<Integer> wumpusIds;
	private Hashtable<Integer, Float> wumpuse;
	
	public Tile(final TileType type, final Vector2 pos) {
		System.out.println("Tile const got: " + pos);
		this.type = type;
		this.pos = pos;
		this.possibleTypes = new HashSet<>();
		this.wumpuse = new Hashtable<>();
	}
	
	public Tile(final TileType type, final int x, final int y) {
		this.type = type;
		this.pos = new Vector2(x, y);
		this.possibleTypes = new HashSet<>();
		this.wumpuse = new Hashtable<>();
	}
	
	/*
	 * Set a KNOWN type
	 * It clears the possible types 
	 */
	public void setTileType(final TileType type) {
		this.type = type;
		this.possibleTypes.clear();
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
	
	public float getNearstWumpusDistance() {
		float dis = 0;
		for(Integer i : getWumpusIds()) {
			if(getWumpusDistance(i) < dis) {
				dis = getWumpusDistance(i);
			}
		}
		return dis;
	}

	/*
	 * @return all possible tiles. Could return NULL!
	 */
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
		if(this.type == TileType.UNKNOWN) 
			this.possibleTypes.add(type);
	}
	
	public void removePossibleType(final TileType type) {
		if(!this.possibleTypes.remove(type)) {
		}; // TODO Error if not there? looks it returns a bool
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(final boolean breeze) {
		this.breeze = breeze;
	}

	public Set<Integer> getWumpusIds() {
		return wumpuse.keySet();
	}
	
	public Hashtable<Integer, Float> getWumpuse() {
		return this.wumpuse;
	}
	
	public float getWumpusDistance(final int id) {
		return wumpuse.get(id);
	}

	public void addWumpusId(final int id, final float distance) {
		this.wumpuse.put(id, distance);
	}
	
	//TODO Possible error if empty?
	public void removeWumpusId(final int id) {
		this.wumpuse.remove(id);
	}
	
	public void removeAllWumpusIds() {
			this.wumpuse.clear();
	}
	
	public String toString() {
		return "Tile: type: " + this.type + " possible types: " + this.possibleTypes + " pos: " + this.pos;
	}
}










