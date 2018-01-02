package de.fh;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.sun.swing.internal.plaf.metal.resources.metal;

import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.WumpusStartInfo;

/*
 * TODO: No growing List! IndexOutOfBoundsException possible by getNode!!! 
 * TODO: Fix wumpus radar
 */

public class State {
	
	private WumpusStartInfo startInfo;
	private int stenchRadius;
	
	private List<List<Tile>> view;
	private Vector2 hunterPos;

	public State(final List<List<Tile>> view,
			final Vector2 startPos, final WumpusStartInfo startInfo, final Vector2 hunterPos) {
		this.startInfo = startInfo;
		this.view = view;
		this.hunterPos = hunterPos;
		
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
	
	/*
	 * Update the state. It takes the percept from the agent 
	 * and the methode create a current view of the world.
	 *  
	 * If we can be sure there is something (e.g. wall) we set 
	 * the tile type to this. Otherweise we add the tile a
	 * possible of a type.  
	 * 
	 * @param pos hunter position
	 * @param percept current percept from the hunter agent
	 *
	 *
	 *TODO fix NPE
	 */
	public void update(final Vector2 pos, final HunterPercept percept) {
		// If nothing - have to be empty or some action happens like hit by wumpus
		// First statement:  EMPTY as long it's not overriden 
		getTile(pos).setTileType(TileType.EMPTY);
		
		if(percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
		if(percept.isBreeze()) {
			setPossibleTypeAround(pos, TileType.PIT);
			getTile(pos).setBreeze(true);
		}
		
		// If wumpus percept is empty remove alle wumpus around hunter withhin stench distance
		if(percept.getWumpusStenchRadar().isEmpty()) {
			System.out.println("EMPTY");
			for(List<Tile> l : this.view) {
				for(Tile n : l) {
					if(n == null) continue;
					//Manhatten distance
					int distance =  Math.abs(pos.getX() - n.getPosX()) 
			        		+ Math.abs(pos.getY() - n.getPosY());
					if(distance <= startInfo.getStenchDistance())
						//TODO: It removes something it shouldn't  NOPE?
						n.removeAllWumpusIds();
				}
			}
		// TODO: if the wumpus dosn*t move it can't be on a old place where
		// the hunter was
		} else {
			// TODO: Dont know if right key<->Value // key=id
			int hPosX = pos.getX();
			int hPosY = pos.getY();
			int radius = this.stenchRadius;
			for(Map.Entry<Integer, Integer> id : percept.getWumpusStenchRadar().entrySet()) {
				
				for(int y = hPosY - radius; y < hPosY + radius; y++) {
					for(int x = hPosX - radius; x  < hPosX + radius; x++) {
						Tile n = getTile(x, y);
						int distance =  Math.abs(hPosX - n.getPosX()) 
				        		+ Math.abs(hPosY - n.getPosY());
						if(distance == id.getValue()) {
							n.addWumpusId(id.getKey());
						}
					}
				}
				
			}
		}
		
		if(percept.isGlitter()) getTile(pos).setTileType(TileType.GOLD);
	}
	
	private void setPossibleTypeAround(final Vector2 pos, final TileType type) {
		int x = pos.getX();
		int y = pos.getY();
		
		//North
		getTile(x, (y - 1)).addPossibleType(type);
		//East
		getTile((x + 1), y).addPossibleType(type);
		//South
		getTile(x, (y + 1)).addPossibleType(type);
		//West
		getTile((x - 1), y).addPossibleType(type);
		
		//TODO: add cost/ heuristic values here? 
	}
	
	public Tile getTile(final int x, final int y) {
		Tile n = this.view.get(y).get(x);
		if(n == null) {
			n = new Tile(TileType.UNKNOWN, x, y);
			this.view.get(y).set(x, n);
		}
		return n;
	}
	
	public Tile getTile(final Vector2 pos) {
		Tile n = this.view.get(pos.getY()).get(pos.getX());
		if(n == null) {
			n = new Tile(TileType.UNKNOWN, pos);
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
				Tile n = this.view.get(y).get(x);
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
	 *  IDs numbers = Wumpus
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
				Tile n = this.view.get(y).get(x);
				if(n != null) {
					if(n.getTileType() == TileType.UNKNOWN) {
						//if(n.getPossibleTypes() == null) continue; //TODO stuipd fix?
						// shouldnt be ther something if is it unknown?
						
						// Possible empty if there was a wumpus
						if(n.getPossibleTypes() != null) {
							s += "(";
							for(TileType t : n.getPossibleTypes()) {
								//if(t == null) break; //TODO need it by foreach?
								s += t.getSymbol();
							}
							s += ")";
						}
					} else {
						s += " " + n.getTileType().getSymbol() + " ";
					}
					// --- Wumpus ---
					if(n.getWumpusIds() != null && !n.getWumpusIds().isEmpty()) {
						s += "(";
						for(Integer i : n.getWumpusIds()) {
							s += i;
						}
						s += ")";
					}
					// --- Wumpus END ---
					if(n.isBreeze()) {
						s = s.substring(0, s.length() - 1);
						s += "B";
					}
				} else {
					s += "e   ";
				}
				// --- Hunter ---
				if(x == hunterPos.getX() && y == hunterPos.getY()) {
					s = s.substring(0, s.length() - 1);
					s += "H ";
				}
				// --- Hunter END ---
			}
			s += "\n";
		}
		s += "----------------------------------------------\n";
		return s;
	}
}





