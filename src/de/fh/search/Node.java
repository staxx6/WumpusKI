package de.fh.search;

import de.fh.Tile;

/*
 * Node holds the representing tile and the values for the search.
 * Nodes don't need current states. Every change would trigger a new 
 * search. 
 */
public class Node {

	private Tile tile;
	private Node parentNode;
	private NodeValue nodeValue; // null
	
	public Node(final Tile tile) {
		this.tile = tile;
	}
	
	public Node(final Tile tile, final Node parentNode) {
		this(tile);
		this.parentNode = parentNode;
	}
	
	public Tile getTile() {
		return this.tile;
	}
	
	public NodeValue getValue() {
		return this.nodeValue;
	}
	
	/*
	 * Returns the parent node. This could return a null value
	 * if root node!  
	 */
	public Node getParentNode() {
		return parentNode;
	}
	
	/*
	 * Weak comparing but should be enough
	 * TODO: Untested
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o == this) return true;
		if(!o.getClass().equals(getClass())) return false;
		Node that = (Node) o;
		if(that.getTile().getPosVector().getX() == this.getTile().getPosX() 
			&& that.getTile().getPosVector().getY() == this.getTile().getPosY()) {
			return true;
		} else {
			return false; 
		}
	}
}
