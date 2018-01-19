package de.fh.search;

import java.util.ArrayList;

import de.fh.util.Vector2;
import de.fh.State;
import de.fh.Tile;
import de.fh.TileType;
import de.fh.game.Entity.Direction;

/*
 * This class implement the A* search
 * TODO: Untested
 */
public class Search {

	private Goal goal;
	private SearchValues searchValues;
	
	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;
	
	private State state; 
	
	public Search(final Goal goal, final SearchValues searchValues, 
			final State state) {
		openList = new ArrayList<>();
		closedList = new ArrayList<>();
		
		this.goal = goal; // a "U" Tile e.g.
		this.searchValues = searchValues;
		this.state = state;
	}
	
	public Node start(final Vector2 startPos) {
		// root node
		addNode(new Node(this.state.getTile(startPos)));
		
		while(!openList.isEmpty()) {
			Node expansionCandidate = this.openList.get(0);
			this.openList.remove(0);
			this.closedList.add(expansionCandidate); // prevent loops
			
			if(this.goal.isNodeGoal(expansionCandidate)) {
				return expansionCandidate;
			} else {
				expandNode(expansionCandidate);
//				System.out.println("Open: " + this.openList.size());
//				System.out.println("Closed: " + this.closedList.size());
			}
		}
//		return null;
		// TODO: Level could have no more unknowns (with given risk)
		// Possible level END
		// - have to take more risk here
		throw new NullPointerException("Couldn't find any goal node!");
	}
	
	/*
	 * Expand the node. It prefers the north node
	 */
	private void expandNode(final Node previewNode) {
		Direction dir[];
		dir = Direction.values();
		for(int i = 3; i >= 0; i-- ) {
			
			Vector2 newPos = calcNewPos(previewNode.getTile().getPosVector(),
					dir[i]);
			
			// TODO: Missing checks for lower postions
			if(newPos.getX() > this.state.getCurrViewSizeLimit().getX() 
					|| newPos.getY() > this.state.getCurrViewSizeLimit().getY()
						|| false
							|| false) {
				continue;
			}
			
			// Ignore walls and pits
			// preview node cant be a wall or something
			// make no sense @see PacmanSuche
			
			Tile tile = state.getTile(newPos);
//			Node successor = new Node(state.getTile(newPos), previewNode);
			Node successor = new Node(tile, previewNode);
			TileType sucType = successor.getTile().getTileType();
			
			if(sucType == TileType.WALL || sucType == TileType.PIT) {
				this.closedList.add(successor);
				continue;
			}
			
			for(Node n : this.closedList) {
				if(successor.equals(n)) {
					continue;
				}
			}
			
			evaluateNode(successor);
			addNode(successor);
		}
	}
	
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
	
	// TODO: Risk tolerance bad like this, its with pathCost // is it?
	// Evaluate node value with A*-Algo
	private void evaluateNode(final Node expansionCandidate) {
		Tile tile = expansionCandidate.getTile();
		NodeValue nodeValue = expansionCandidate.getValue();
		float risk = 0;
		
		if(tile.getTileType() == TileType.UNKNOWN) {
//			if(tile.getPossibleTypes() != null) {
				for(TileType type : tile.getPossibleTypes()) {
					if(type == TileType.WALL) risk += this.searchValues.getWall();
					else if(type == TileType.PIT) risk += this.searchValues.getPit();
				}
//			}
		}
		// risk unchanged if type empty = 0
		
		if(tile.getWumpusIds() != null && !tile.getWumpusIds().isEmpty()) {
			for(int id : tile.getWumpusIds()) {
				risk += this.searchValues.getWumpusDistanceFac() 
						/ tile.getWumpusDistance(id);
			}
		}
		
		//TODO: calc path cost
		float pathCost = nodeValue.getPathCost() 
				+ expansionCandidate.getParentNode().getValue().getPathCost() 
					+ 1;
		nodeValue.setPathCost(pathCost);
		
		float distanceCost = 0;
		//TODO: shiiiit
		if(this.goal instanceof GoalLocation) {
			// Manhattan distance
			Vector2 goalLoc = ((GoalLocation) this.goal).getLocation();
	        distanceCost = Math.abs(goalLoc.getX() - expansionCandidate.getTile().getPosX()) 
	        		+ Math.abs(goalLoc.getY() - expansionCandidate.getTile().getPosY());
	        nodeValue.setPathCost(distanceCost); // Overwrite
	        
	        if(tile.getTileType() == TileType.UNKNOWN) {
	        	risk += this.searchValues.getUnknown();
	        }
		}		
		nodeValue.setRisk(risk);
	}
	
	// Insert the node to openList, value priority (from A*-Algo)
	private void addNode(final Node expansionCandidate) {
		if(expansionCandidate.getValue().getRisk() 
				> goal.getRiskTolerance()) {
			this.closedList.add(expansionCandidate);
			return;
		}
		
		int newIndex = 0;
		for(Node n : this.openList) {
			// TODO not A*
			if(n.getValue().get() < expansionCandidate.getValue().get()) {
//			if(n.getValue().get() < expansionCandidate.getValue().getPathCost()) {
				newIndex++;
			} else {
				break;
			}
		}
		this.openList.add(newIndex, expansionCandidate);
	}
}










