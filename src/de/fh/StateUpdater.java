package de.fh;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.WumpusStartInfo;

/*
 * TODO No growing List! IndexOutOfBoundsException possible by getNode!!! 
 */

public class StateUpdater {
	
	private WumpusStartInfo startInfo;
	
	private List<List<Node>> view;
	private Hashtable<Integer, Integer> stenchRadar;
	private HunterPercept percept;

	public StateUpdater(final List<List<Node>> view, final HunterPercept percept,
			final Vector2 startPos, final Hashtable<Integer, Integer> stenchRadar, 
				WumpusStartInfo startInfo) {
		this.startInfo = startInfo;
		this.view = view;
		this.percept = percept;
		this.stenchRadar = stenchRadar;
		
		// TODO: Check Variables
		// Create Lists and fill it with nodes
		// TODO: List is null ? ERROR
		if(this.view == null) {
 			this.view = new ArrayList<>();
 			for(int y = 0; y < startPos.getY() * 2; y++) {
 				this.view.add(new ArrayList<>());
 				for(int x = 0; x < startPos.getX() * 2; x++) {
 					this.view.get(y).add(null);
 					if(y == startPos.getY() && x == startPos.getX())
 						getNode(x, y).setTileType(TileType.HUNTER);
 					else
 						getNode(x, y).setTileType(TileType.UNKNOWN);
 				}
 			}
 		}
	}
	
	public void update(final Vector2 pos) {
		if(this.percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
		if(this.percept.isBreeze()) {
			setPossibleTypeAround(pos, TileType.PIT);
			getNode(pos).setBreeze(true);
		}
		
		if(this.stenchRadar.isEmpty()) {
			//TODO remove possible Wumpus tiles around startInfo.getStenchDistance()
		} else {
			//TODO set possible wumpuis around stench radius
			//TODO -> update positions
			/*
			for(Map.Entry<Integer, Integer> g : stenchRadar.entrySet()){
				System.out.println(g.getKey() + ":\t\t" + g.getValue() );
			}
			*/
		}
		
		// If nothing - have to be empty or some action happens like hit by wumpus
		getNode(pos).setTileType(TileType.EMPTY);
	}
	
	private void setPossibleTypeAround(final Vector2 pos, final TileType type) {
		int x = pos.getX();
		int y = pos.getY();
		
		//North
		addPossibleTile(x, --y, type);
		//East
		addPossibleTile(++x, y, type);
		//South
		addPossibleTile(x, ++y, type);
		//West
		addPossibleTile(--x, y, type);
		
		//TODO: add cost/ heuristic value here? 
	}
	
	private void addPossibleTile(final int x, final int y, final TileType type) {
		getNode(x, y).addPossibleType(type);
	}
	
	private Node getNode(final int x, final int y) {
		Node n = this.view.get(y).get(x);
		if(n == null) n = new Node(TileType.UNKNOWN, x, y);
		return n;
	}
	
	private Node getNode(final Vector2 pos) {
		Node n = this.view.get(pos.getY()).get(pos.getX());
		if(n == null) n = new Node(TileType.UNKNOWN, pos);
		return n;
	}
}





