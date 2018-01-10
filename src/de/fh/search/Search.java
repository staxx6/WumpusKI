package de.fh.search;

import java.util.ArrayList;

import de.fh.util.Vector2;
import de.fh.State;
import de.fh.TileType;

/*
 * This class implement the A* search
 * TODO: Untested
 */
public class Search {
	
	private enum Direction {
		NORTH, EAST, SOUTH, WEST;
	}

	private Goal goal;
	
	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;
	
	private State state; 
	
	public Search(final Goal goal) {
		openList = new ArrayList<>();
		closedList = new ArrayList<>();
		
		this.goal = goal; // a U/e Tile e.g.
	}
	
	public Node start(final Vector2 startPos) {
		
		// root node
		addNode(new Node(state.getTile(startPos)));
		
		while(!openList.isEmpty()) {
			// get first node from (sorted) list and remove it (pop)
			Node expansionCandidate = this.openList.get(0);
			this.openList.remove(0);
			this.closedList.add(expansionCandidate); // prevent loops
			
			if(this.goal.isNodeGoal(expansionCandidate)) {
				return expansionCandidate;
			} else {
				expandNode(expansionCandidate);
			}
		}
		return null;
	}
	
	private void expandNode(final Node previewNode) {
		for(Direction d : Direction.values()) {
			Vector2 newPos = calcNewPos(previewNode.getTile().getPosVector(), d);
			
			// Ignore walls and pits
			// preview node cant be a wall or something
			// make no sense @see PacmanSuche
//			Tile newTile = this.state.getTile(newPos);
//			if(newTile.getTileType() == TileType.WALL 
//					|| newTile.getTileType() == TileType.PIT)
//				return;
			
			Node successor = new Node(state.getTile(newPos), previewNode);
			TileType sucType = successor.getTile().getTileType();
			
			if(sucType == TileType.WALL || sucType == TileType.PIT)
				return;
			
			for(Node n : this.closedList) {
				if(successor.equals(n)) {
					return;
				}
			}
			
			evaluateNode(successor);
			addNode(successor);
		}
	}
	
	//TODO test pos
	private Vector2 calcNewPos(final Vector2 oldPos, final Direction direction) {
		Vector2 pos;
		switch(direction) {
			case NORTH: pos = new Vector2(oldPos.getX(), oldPos.getY() - 1);
						break;
			case EAST: 	pos = new Vector2(oldPos.getX() + 1, oldPos.getY());
						break;
			case SOUTH: pos = new Vector2(oldPos.getX(), oldPos.getY() + 1);
						break;
			case WEST:	pos = new Vector2(oldPos.getX() - 1, oldPos.getY());
						break;
			default: throw new IllegalArgumentException("ERROR: Direction dosn't exist");
		}
		return pos;
	}
	
	// Insert the node to openList, value priority (from A*-Algo)
	private void addNode(final Node expansionCandidate) {
		int newIndex = 0;
		for(Node n : this.openList) {
			if(n.getValue() < expansionCandidate.getValue()) {
				newIndex++;
			} else {
				break;
			}
		}
		this.openList.add(newIndex, expansionCandidate);
	}
	
	private void evaluateNode(final Node expansionCandidate) {
		//TODO: calc risk here
		//TODO: calc path cost
	}
}










