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
	private NodeValue nodeValue;
	
	public Node(final Tile tile) {
		this.tile = tile;
	}
	
	public Node(final Tile tile, final Node parentNode) {
		this(tile);
		this.parentNode = parentNode;
	}
}
