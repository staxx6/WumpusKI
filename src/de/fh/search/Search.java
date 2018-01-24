package de.fh.search;

import java.util.ArrayList;

import de.fh.util.Vector2;
import de.fh.State;
import de.fh.Tile;
import de.fh.TileType;
import de.fh.game.Entity.Direction;

/*
 * This class implement the A* search
 */
public class Search {

	private Goal goal;
	private SearchValues searchValues;

	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;

	private State state;

	private boolean debugOut = true;
	private boolean extraDebug = true;

	public Search(final Goal goal, final SearchValues searchValues, final State state) {
		openList = new ArrayList<>();
		closedList = new ArrayList<>();

		this.goal = goal; // a "U" Tile e.g.
		this.searchValues = searchValues;
		this.state = state;
	}

	public Node start(final Vector2 startPos) {
		// root node
		addNode(new Node(this.state.getTile(startPos)));

		while (!openList.isEmpty()) {
			Node expansionCandidate = this.openList.get(0);
			this.openList.remove(0);
			this.closedList.add(expansionCandidate); // prevent loops

			if (this.goal.isNodeGoal(expansionCandidate)) {
				return expansionCandidate;
			} else {
				expandNode(expansionCandidate);
			}
		}
		// TODO: Level could have no more unknowns (with given risk)
		// Possible level END
		// - have to take more risk here
		return null;
	}

	/*
	 * Expand the node, clockwise
	 */
	private void expandNode(final Node previewNode) {
		if (this.debugOut)
			System.out.println("@expandNode (" + previewNode.getTile().getPosVector() + "): ");
		Direction dir[];
		dir = Direction.values();
		for (int i = 3; i >= 0; i--) {

			Vector2 newPos = calcNewPos(previewNode.getTile().getPosVector(), dir[i]);
			if (this.debugOut)
				System.out.print("Pos " + newPos + " got: ");

			if (newPos.getX() >= this.state.getCurrViewSizeLimit().getX()
					|| newPos.getY() >= this.state.getCurrViewSizeLimit().getY() || newPos.getX() < 0
					|| newPos.getY() < 0) {
				if (this.debugOut) {
					System.out.println("OUT (out of level/array limits)");
				}
				continue;
			}

			// Ignore walls and pits
			// preview node cant be a wall or something
			// make no sense @see PacmanSuche

			Node successor = new Node(state.getTile(newPos), previewNode);
			TileType sucType = successor.getTile().getTileType();

			if (sucType == TileType.WALL || sucType == TileType.PIT) {
				this.closedList.add(successor);
				if (this.debugOut)
					System.out.println("OUT (wall/pit)");
				continue;
			}

			if (this.closedList.contains(successor)) {
				if (this.debugOut)
					System.out.println("OUT (is already in closedList)");
				continue;
			}

			evaluateNode(successor);
			if (this.debugOut)
				System.out.print("[" + successor.getValue() + "]");
			addNode(successor);
		}
	}

	private Vector2 calcNewPos(final Vector2 oldPos, final Direction direction) {
		Vector2 pos;
		switch (direction) {
		case NORTH:
			pos = new Vector2(oldPos.getX(), oldPos.getY() - 1);
			break;
		case EAST:
			pos = new Vector2(oldPos.getX() + 1, oldPos.getY());
			break;
		case SOUTH:
			pos = new Vector2(oldPos.getX(), oldPos.getY() + 1);
			break;
		case WEST:
			pos = new Vector2(oldPos.getX() - 1, oldPos.getY());
			break;
		default:
			throw new IllegalArgumentException("ERROR: Direction dosn't exist");
		}
		return pos;
	}

	// Evaluate node value with A*-Algo
	private void evaluateNode(final Node expansionCandidate) {
		if (this.extraDebug)
			System.out.print(" { ");
		Tile tile = expansionCandidate.getTile();
		NodeValue nodeValue = expansionCandidate.getValue();

		float risk = 0;
		float pathCost = 0;
		float distanceCost = 0;

		if (this.goal.useRisk) {
			if (tile.getTileType() == TileType.UNKNOWN) {
				for (TileType type : tile.getPossibleTypes()) {
					if (type == TileType.WALL) {
						if (this.extraDebug)
							System.out.print("posWall+" + this.searchValues.getWall());
						risk += this.searchValues.getWall();
					} else if (type == TileType.PIT) {
						if (this.extraDebug)
							System.out.print(" posPit+" + this.searchValues.getPit());
						risk += this.searchValues.getPit();
					}
				}
			}

			if (tile.getWumpusIds() != null && !tile.getWumpusIds().isEmpty()) {
				float wumpusRisk = 0;
				for (int id : tile.getWumpusIds()) {
					System.out.println(" id: " + id + " has distance: " + tile.getWumpusDistance(id) + " ");
					if (tile.getWumpusDistance(id) != 0) {
						if (this.extraDebug)
							System.out.print(
									" wumpi+" + this.searchValues.getWumpusDistanceFac() / tile.getWumpusDistance(id));

						if (tile.getWumpusDistance(id) > wumpusRisk) {
							wumpusRisk = tile.getWumpusDistance(id);
						}
					}
				}
				risk += this.searchValues.getWumpusDistanceFac() / wumpusRisk;
			}
			if (this.state.getHistoryStench(tile.getPosVector()) != 0) {
				if (this.extraDebug)
					System.out.print(" historyWumpi+" + this.searchValues.getWumpusDistanceFac()
							/ this.state.getHistoryStench(tile.getPosVector()));
				risk += this.searchValues.getWumpusDistanceFac() / this.state.getHistoryStench(tile.getPosVector()) / 2;
			}
		}

		if (this.goal.usePathCost) {
			pathCost = nodeValue.getPathCost() + expansionCandidate.getParentNode().getValue().getPathCost() + 1;
		}

		if (this.goal.useDistanceCost) {
			Vector2 goalLoc = ((GoalLocation) this.goal).getLocation(); // TODO: Dont like this cast here
			distanceCost = Math.abs(goalLoc.getX() - expansionCandidate.getTile().getPosX())
					+ Math.abs(goalLoc.getY() - expansionCandidate.getTile().getPosY());

			if (tile.getTileType() == TileType.UNKNOWN) {
				pathCost += this.searchValues.getUnknown();
			}
		}

		nodeValue.setRisk(risk);
		nodeValue.setPathCost(pathCost);
		nodeValue.setDistanceCost(distanceCost);
		if (this.extraDebug)
			System.out.println(" } ");
	}

	// Insert the node to openList, value priority (from A*-Algo)
	private void addNode(final Node expansionCandidate) {
		if (expansionCandidate.getValue().getRisk() > goal.getRiskTolerance()) {
			this.closedList.add(expansionCandidate);
			if (this.debugOut)
				System.out.println(" OUT (risk too high)");
			return;
		}

		int newIndex = 0;
		for (Node n : this.openList) {
			if (n.getValue().getAstar() < expansionCandidate.getValue().getAstar()) {
				newIndex++;
			} else {
				break;
			}
		}
		this.openList.add(newIndex, expansionCandidate);
		if (this.debugOut)
			System.out.println(" placed in openList index: " + newIndex);
	}
}
