package de.fh.search;

import java.util.ArrayList;
import java.util.List;

import de.fh.game.Tile;
import de.fh.util.Vector2;
import de.fh.State;

/*
 * This class implement the A* search
 */
public class Search {

	private Node goalNode;
	
	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;
	
	private State state; 
	
	public Search(final Node goalNode) {
		openList = new ArrayList<>();
		closedList = new ArrayList<>();
		
		this.goalNode = goalNode; // a U/e Tile e.g.
	}
	
	public Node start(final Vector2 startPos) {
		
		// root node
		addNode(new Node(state.getTile(startPos)));
		
		while(!openList.isEmpty()) {
			// get first node from (sorted) list and remove it (pop)
			Node expansionCandidate = this.openList.get(0);
			this.openList.remove(0);
			this.closedList.add(expansionCandidate); // prevent loops
			
			if(expansionCandidate.isGoal(this.goalNode)) {
				return expansionCandidate;
			} else {
				expandNode(expansionCandidate);
			}
		}
	}
	
	private void addNode(final Node expansionCandidate) {
		//TODO
	}
}










