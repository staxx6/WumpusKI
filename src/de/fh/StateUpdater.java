package de.fh;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.WumpusStartInfo;

/*
 * TODO: No growing List! IndexOutOfBoundsException possible by getNode!!! 
 * TODO: Rename to "State"
 * TODO: Fix wumpus radar
 */

public class StateUpdater {
	
	private WumpusStartInfo startInfo;
	private int stenchRadius;
	
	private List<List<Node>> view;
//	private Hashtable<Integer, Integer> stenchRadar;
//	private HunterPercept percept;

	public StateUpdater(final List<List<Node>> view,
			final Vector2 startPos, final Hashtable<Integer, Integer> stenchRadar, 
				final WumpusStartInfo startInfo) {
		this.startInfo = startInfo;
		this.view = view;
//		this.percept = percept;
//		this.stenchRadar = stenchRadar;
		
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
	
	public void update(final Vector2 pos, final HunterPercept percept) {
		// If nothing - have to be empty or some action happens like hit by wumpus
		// First statement:  EMPTY as long it's not overriden 
		getNode(pos).setTileType(TileType.EMPTY);
		
		if(percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
		if(percept.isBreeze()) {
			setPossibleTypeAround(pos, TileType.PIT);
			getNode(pos).setBreeze(true);
		}
		
		/*
		//TODO: Not tested
		if(percept.getWumpusStenchRadar().isEmpty()) {
			System.out.println("EMPTY");
			for(List<Node> l : this.view) {
				for(Node n : l) {
					if(n == null) continue;
					//Manhatten distance
					int distance =  Math.abs(pos.getX() - n.getPosX()) 
			        		+ Math.abs(pos.getY() - n.getPosY());
					if(distance <= startInfo.getStenchDistance())
						//TODO: It removes something it shouldn't 
						n.removeAllWumpusIds();
				}
			}
		} else {
			// TODO: Dont know if right key<->Value // key=id
			int hPosX = pos.getX();
			int hPosY = pos.getY();
			for(Map.Entry<Integer, Integer> i : percept.getWumpusStenchRadar().entrySet()) {
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
		if(percept.isGlitter()) getNode(pos).setTileType(TileType.GOLD);
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
	
	public Node getNode(final int x, final int y) {
		Node n = this.view.get(y).get(x);
		if(n == null) {
			n = new Node(TileType.UNKNOWN, x, y);
			this.view.get(y).set(x, n);
		}
		return n;
	}
	
	public Node getNode(final Vector2 pos) {
		Node n = this.view.get(pos.getY()).get(pos.getX());
		if(n == null) {
			n = new Node(TileType.UNKNOWN, pos);
			this.view.get(pos.getY()).set(pos.getX(), n);
		}
		return n;
	}
	
	/*
	 * Reaturns a String with known tiles as a matrix
	 * Null tiles are shwon with a small "e"
	 *  E = Empty
	 *  H = Hunter
	 *  W = Wall
	 *  G = Gold
	 *  X = Wumpus
	 *  P = Pit
	 *  B = Breeze
	 *  U = Unkown (unproven tiles)
	 */
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
	
	/*
	 * Reaturns a String with possible and known tiles as a matrix
	 * e.g "(WPX)" (They are the UNKNOWN tiles)
	 * Null tiles are shwon with a small "e"
	 *  E = Empty
	 *  H = Hunter
	 *  W = Wall
	 *  G = Gold
	 *  X = Wumpus
	 *  P = Pit
	 *  B = Breeze
	 */
	public String toStringPossible() {
		String s = "------------ Current View - Possible --------------\n";
		s += "-  ";
		for(int i = 0; i < this.view.size(); i++) {
			s += i + "  ";
		}
		s += "\n";
		for(int y = 0; y < this.view.size(); y++) {
			s += y + ". ";
			for(int x = 0; x < this.view.size(); x++) {
				Node n = this.view.get(y).get(x);
				if(n != null) {
					if(n.getTileType() == TileType.UNKNOWN) {
						s += "(";
						for(TileType t : n.getPossibleTypes()) {
							if(t == null) break; //TODO need it by foreach?
							if(t == TileType.WUMPUS) {
								for(Integer i : n.getWumpusIds())
									s += i + ",";
							} else {
								s += t.getSymbol();
							}
						}
						s += ")";
					} else {
						s += " " + n.getTileType().getSymbol() + " ";
					}
					if(n.isBreeze()) {
						s = s.substring(0, s.length() - 1);
						s += "B";
					}
				} else {
					s += "e  ";
				}
			}
			s += "\n";
		}
		s += "----------------------------------------------\n";
		return s;
	}
}





