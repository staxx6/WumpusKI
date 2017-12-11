package de.fh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fh.util.Vector2;

/*
 * TODO: Naming fail?
 */

public class Node {
	private TileType type;
	private Vector2 pos;
	private List<TileType> possibleTypes;
	private boolean breeze = false;
	private Set<Integer> wumpusIds;
	
	public Node(final TileType type, final Vector2 pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public Node(final TileType type, final int x, final int y) {
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

	public List<TileType> getPossibleTypes() {
		return possibleTypes;
	}

	public void setPossibleTypes(List<TileType> possibleTypes) {
		if(this.type == TileType.UNKNOWN)
			this.possibleTypes = possibleTypes;
		else
			System.out.println("Don't need to add possible types, type should be clear!");
	}
	
	public void addPossibleType(final TileType type) {
		if(this.possibleTypes == null) this.possibleTypes = new ArrayList<>();
		if(this.type == TileType.UNKNOWN)
			this.possibleTypes.add(type);
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(boolean breeze) {
		this.breeze = breeze;
	}

	/*
	 * @return Could be a null return 
	 */
	public Set<Integer> getWumpusIds() {
		return wumpusIds;
	}

	public void addWumpusId(final int id) {
		if(this.wumpusIds == null) this.wumpusIds = new HashSet<>();
		this.wumpusIds.add(id);
	}
	
	public void removeWumpusId(final int id) {
		this.wumpusIds.remove(id);
	}
}
