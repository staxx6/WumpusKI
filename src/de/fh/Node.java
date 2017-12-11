package de.fh;

import java.util.List;

import de.fh.util.Vector2;

public class Node {
	private TileType type;
	private Vector2 pos;
	private List<TileType> possibleStates;
	
	public Node(final TileType type, final Vector2 pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public Node(final TileType type, final int x, final int y) {
		this.type = type;
		this.pos = new Vector2(x, y);
	}
	
	public void setTileType(final TileType type) {
		this.type = type;
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

	public List<TileType> getPossibleStates() {
		return possibleStates;
	}

	public void setPossibleStates(List<TileType> possibleStates) {
		this.possibleStates = possibleStates;
	}
}
