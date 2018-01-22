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
 * TODO: Fix wumpus radar
 */

public class State {
	
	private WumpusStartInfo startInfo;
	private int stenchRadius;
	
	private List<List<Tile>> view;
	private Vector2 currViewSizeLimit; // (x, y)
	private Vector2 hunterPos;

	public State(final Vector2 startPos, final WumpusStartInfo startInfo, 
			final Vector2 hunterPos) {
		this.startInfo = startInfo;
		this.hunterPos = hunterPos;
		
		this.stenchRadius = startInfo.getStenchDistance();
		
		// Create Lists and fill it with null 
		if(this.view == null) { // TODO: Not needed
 			this.view = new ArrayList<>();
 			for(int y = 0; y < startPos.getY() * 2; y++) {
 				this.view.add(new ArrayList<>());
 				for(int x = 0; x < startPos.getX() * 2; x++) {
 					this.view.get(y).add(null);
 				}
 			}
 		}
		this.currViewSizeLimit = new Vector2(this.view.get(0).size(), this.view.size());
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
	 */
	public void update(final Vector2 pos, final HunterPercept percept) {
		if(percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
		if(percept.isBreeze()) {
			setPossibleTypeAround(pos, TileType.PIT);
			getTile(pos).setBreeze(true);
		} else {
			removePossibleTypeAround(pos, TileType.PIT);
		}
		if(percept.isGlitter()) getTile(pos).setTileType(TileType.GOLD);
		
		
		
		removeWumpusAll();
		updateWumpus(percept, pos);
		if(pos.getX() == 20 && pos.getY() == 18) {
			System.out.println("as");;
		}
	}
	
	private void removeWumpusAll() {
		for(int y = 0; y < this.view.size(); y++) {
			for(int x = 0; x < this.view.get(0).size(); x++) {
				Tile t = this.view.get(y).get(x);
				if(t != null && t.getWumpuse() != null)
					t.getWumpuse().clear();
			}
		}
	}
	
	private void updateWumpus(final HunterPercept percept, final Vector2 pos) {
		// If wumpus percept is empty remove alle wumpus around hunter within stench distance
//		if(percept.getWumpusStenchRadar().isEmpty()) {
//			for(List<Tile> l : this.view) {
//				for(Tile n : l) {
//					if(n == null) continue;
//					//Manhatten distance
//					int distance =  Math.abs(pos.getX() - n.getPosX()) 
//			        		+ Math.abs(pos.getY() - n.getPosY());
//					if(distance <= startInfo.getStenchDistance())
//						n.removeAllWumpusIds();
//				}
//			}
//		} else {
			// key=id value=distance
			int hPosX = pos.getX();
			int hPosY = pos.getY();
			int radius = this.stenchRadius;
			for(Map.Entry<Integer, Integer> id : percept.getWumpusStenchRadar().entrySet()) {
				
				// Go around the hunter and add the wumpus
				for(int y = hPosY - radius; y <= hPosY + radius; y++) {
					for(int x = hPosX - radius; x  <= hPosX + radius; x++) {
						
						Tile t = getTile(x, y);
						int distance =  Math.abs(hPosX - t.getPosX()) 
				        		+ Math.abs(hPosY - t.getPosY());
						
						if(distance <= radius)
							t.addWumpusId(id.getKey(), radius - distance);
						
						// Here is the wumpus with given distance/ stench
//						if(distance == id.getValue()) {
////							t.addWumpusId(id.getKey(), id.getValue()); // TODO: not needed
//							
//							// Go around the possible wumpus tile and set stench values
//							// which is needed for the search
//							System.out.println("Have set wumpus set @" + x + "x und " + "y");
//							
//							for(int wY = t.getPosY() - radius; wY < t.getPosY() + radius; wY++) {
//								for(int wX = t.getPosX() - radius; wX < t.getPosX() + radius; wX++) {
//									
//									
//									Tile tW = getTile(wX, wY);
//									int manDistance = Math.abs(t.getPosX() - tW.getPosX()) 
//							        		+ Math.abs(t.getPosY() - tW.getPosY());
//									
//									if(manDistance <= radius) {
//										tW.addWumpusId(id.getKey(), manDistance);
//									}
//								}
//							}
//						} 
					}
//				}
			}
			System.out.println("HELLO " + getTile(18, 18).getWumpuse());
		}
		showWumpusDebug();
	}
	
	private void showWumpusDebug() {
		for(int i = 0; i < view.size(); i++) {
			for(int j = 0; j < view.size(); j++) {
				
				Tile t = view.get(i).get(j);
				if(t != null) {
					Hashtable<Integer, Integer> w = view.get(i).get(j).getWumpuse();
					if(w != null && !w.isEmpty()) {
						System.out.print(view.get(i).get(j).getWumpuse() + " ");
					} else {
						System.out.print("         ");
					}
				} else {
					System.out.print("         ");
				}
			}
			System.out.println("");
		}
	}
	
	private void setPossibleTypeAround(final Vector2 pos, final TileType type) {
		int x = pos.getX();
		int y = pos.getY();
		
		//North
		if(!getTile(x, (y - 1)).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile(x, (y - 1)).addPossibleType(type);
		//East
		if(!getTile((x + 1), y).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile((x + 1), y).addPossibleType(type);
		//South
		if(!getTile(x, (y + 1)).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile(x, (y + 1)).addPossibleType(type);
		//West
		if(!getTile((x - 1), y).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile((x - 1), y).addPossibleType(type);
	}
	
	// TODO: Test with set shoud be only one possible type
	private void removePossibleTypeAround(final Vector2 pos, final TileType type) {
		int x = pos.getX();
		int y = pos.getY();
		
		//North
		getTile(x, (y - 1)).removePossibleType(type);
		getTile(x, (y - 1)).addPossibleType(TileType.getCounterPart(type));
		//East
		getTile((x + 1), y).removePossibleType(type);
		getTile((x + 1), y).addPossibleType(TileType.getCounterPart(type));
		//South
		getTile(x, (y + 1)).removePossibleType(type);
		getTile(x, (y + 1)).addPossibleType(TileType.getCounterPart(type));
		//West
		getTile((x - 1), y).removePossibleType(type);
		getTile((x - 1), y).addPossibleType(TileType.getCounterPart(type));
	}
	
	/*
	 * Return the tile where the hunter is standing
	 * 
	 * @return tile
	 */
	public Tile getHunterTile() {
		return getTile(this.hunterPos);
	}
	
	/*
	 * Get a tile from position (x, y), if there
	 * isn't a tile it creates a new one.
	 * 
	 * @return tile
	 */
	public Tile getTile(final int x, final int y) {
		
		Tile n = this.view.get(y).get(x);
		
		if(n == null) {
			n = new Tile(TileType.UNKNOWN, x, y);
			this.view.get(y).set(x, n);
		}
		return n;
	}
	
	/*
	 * Get a tile from position (Vector2 pos), if there
	 * isn't a tile it creates a new one.
	 * 
	 * Tile n = this.view.get(pos.getY()).get(pos.getX()); BUGGED why ever
	 * like now better
	 * 
	 * @return tile
	 */
	public Tile getTile(final Vector2 pos) {
		return getTile(pos.getX(), pos.getY());
	}
	
	public Vector2 getCurrViewSizeLimit() {
		return this.currViewSizeLimit;
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
					s += "(";
					if(n.getTileType() == TileType.UNKNOWN) {
						//if(n.getPossibleTypes() == null) continue; //TODO stuipd fix?
						// shouldnt be ther something if is it unknown?
						
						// Possible empty if there was a wumpus
//						if(n.getPossibleTypes() != null) {
							for(TileType t : n.getPossibleTypes()) {
								//if(t == null) break; //TODO need it by foreach?
								s += t.getSymbol();
							}
//						}
					} else {
						s += " " + n.getTileType().getSymbol() + " ";
					}
					// --- Wumpus ---
					if(n.getWumpusIds() != null && !n.getWumpusIds().isEmpty()) {
						for(Integer i : n.getWumpusIds()) {
							s += "X";
						}
					}
					// --- Wumpus END ---
					if(n.isBreeze()) {
						s = s.substring(0, s.length() - 1);
						s += "B";
					}
					s += ")";
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
		s += "----------------------------------------------";
		return s;
	}
}





