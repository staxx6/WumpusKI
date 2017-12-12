package de.fh;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.WumpusStartInfo;

/*
 * TODO No growing List! IndexOutOfBoundsException possible by getNode!!! 
 */

public class StateUpdater {
	
	private WumpusStartInfo startInfo;
	private int stenchRadius;
	
	private List<List<Node>> view;
	private Hashtable<Integer, Integer> stenchRadar;
	private HunterPercept percept;

	
	public StateUpdater(final List<List<Node>> view, final HunterPercept percept,
			final Vector2 startPos, final Hashtable<Integer, Integer> stenchRadar, 
				final WumpusStartInfo startInfo) {
		this.startInfo = startInfo;
		this.view = view;
		this.percept = percept;
		this.stenchRadar = stenchRadar;
		
		this.stenchRadius = startInfo.getStenchDistance();
		
		// Create Lists and fill it with null 
		if(this.view == null) {
 			this.view = new ArrayList<>();
 			for(int y = 0; y < startPos.getY() * 2; y++) {
 				this.view.add(new ArrayList<>());
 				for(int x = 0; x < startPos.getX() * 2; x++) {
 					this.view.get(y).add(null);
 				}
 			}
 		}
	}
	
	public void update(final Vector2 pos) {
		System.out.println("wall around x: " + pos.getX() + " y: " + pos.getY());
		if(this.percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
		if(this.percept.isBreeze()) {
			setPossibleTypeAround(pos, TileType.PIT);
			getNode(pos).setBreeze(true);
		}
		
		//TODO: Not tested
		/*
		if(this.stenchRadar.isEmpty()) {
			for(List<Node> l : this.view) {
				for(Node n : l) {
					if(n == null) continue;
					//Manhatten distance
					int distance =  Math.abs(hunterPos.getX() - n.getPosX()) 
			        		+ Math.abs(hunterPos.getY() - n.getPosY());
					if(distance <= startInfo.getStenchDistance())
						n.removeAllWumpusIds();
				}
			}
		} else {
			// TODO: Dont know if right key<->Value
			int hPosX = this.hunterPos.getX();
			int hPosY = this.hunterPos.getY();
			for(Map.Entry<Integer, Integer> i : stenchRadar.entrySet()) {
				for(int y = hPosY - this.stenchRadius; y < hPosY + this.stenchRadius; y++) {
					for(int x = hPosX - this.stenchRadius; x  < hPosX + this.stenchRadius; x++) {
						Node n = getNode(x, y);
						int distance =  Math.abs(hPosX - n.getPosX()) 
				        		+ Math.abs(hPosY - n.getPosY());
						if(distance == i.getValue()) {
							n.addWumpusId(i.getKey());
						}
					}
				}
			}
		}
		 */
		
		// If nothing - have to be empty or some action happens like hit by wumpus
		getNode(pos).setTileType(TileType.EMPTY);
		System.out.println(toStringKnown());
	}
	
	private void setPossibleTypeAround(final Vector2 pos, final TileType type) {
		int x = pos.getX();
		int y = pos.getY();
		
		//North
		getNode(x, (y - 1)).addPossibleType(type);
		//East
		getNode((x + 1), y).addPossibleType(type);
		//South
		getNode(x, (y + 1)).addPossibleType(type);
		//West
		getNode((x - 1), y).addPossibleType(type);
		
		//TODO: add cost/ heuristic values here? 
	}
	
	private Node getNode(final int x, final int y) {
		Node n = this.view.get(y).get(x);
		if(n == null) {
			n = new Node(TileType.UNKNOWN, x, y);
			this.view.get(y).set(x, n);
		}
		return n;
	}
	
	private Node getNode(final Vector2 pos) {
		Node n = this.view.get(pos.getY()).get(pos.getX());
		if(n == null) {
			n = new Node(TileType.UNKNOWN, pos);
			this.view.get(pos.getY()).set(pos.getX(), n);
		}
		return n;
	}
	
	public String toStringKnown() {
		String s = "------------ Current View - Known --------------\n";
		s += "-  ";
		for(int i = 0; i < this.view.size(); i++) {
			s += i + " ";
		}
		s += "\n";
		for(int y = 0; y < this.view.size(); y++) {
			s += y + ". ";
			for(int x = 0; x < this.view.size(); x++) {
				Node n = this.view.get(y).get(x);
				if(n != null) {
					s += n.getTileType().getSymbol();
					if(n.isBreeze()) s += "B";
					s += " ";
				} else {
					s += "e ";
				}
			}
			s += "\n";
		}
		s += "\n----------------------------------------------\n";
		return s;
	}
	
	// TODO: fix like toStringKnown
	public String toStringPossible() {
		String s = "------------ Current View - Possible --------------\n";
		for(List<Node> list : this.view) {
			for(Node n : list) {
				if(n == null) {
					s += "e ";
					continue;
				}
				
				if(n.getTileType() == TileType.UNKNOWN) {
					for(TileType t : n.getPossibleTypes()) {
						s += "(";
						if(t == TileType.WUMPUS) {
							for(Integer i : n.getWumpusIds())
								s += i + ",";
						} else {
							s += t.getSymbol();
						}
						s += ") ";
					}
				}
				
				s += n.getTileType().getSymbol() + " ";
			}
			s += "\n";
		}
		s += "----------------------------------------------\n";
		return s;
	}
}





