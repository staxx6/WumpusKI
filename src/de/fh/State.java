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
	private HunterMoveHelper moveHelper;

	private HunterPercept percept;
	private MyWumpusAgent agent;
	
	public State(final Vector2 startPos, final WumpusStartInfo startInfo, 
			final HunterMoveHelper moveHelper, final HunterPercept percept,
				final MyWumpusAgent agent) {
		this.startInfo = startInfo;
		this.moveHelper = moveHelper;
		this.percept = percept;
		this.agent = agent;

		this.stenchRadius = startInfo.getStenchDistance();

		// Create Lists and fill it with null
		if (this.view == null) { // TODO: Not needed
			this.view = new ArrayList<>();
			for (int y = 0; y < startPos.getY() * 2; y++) {
				this.view.add(new ArrayList<>());
				for (int x = 0; x < startPos.getX() * 2; x++) {
					this.view.get(y).add(null);
				}
			}
		}
		this.currViewSizeLimit = new Vector2(this.view.get(0).size(), this.view.size());
	}

	public void removeWumpusAll() {
		for (int y = 0; y < this.view.size(); y++) {
			for (int x = 0; x < this.view.get(0).size(); x++) {
				Tile t = this.view.get(y).get(x);
				if (t != null && t.getWumpuse() != null) {
					t.removeAllWumpusIds();
				}
			}
		}
	}

	public void setWumpusStench() {
		System.out.println("radar says: " + percept.getWumpusStenchRadar());

		int hPosX = this.moveHelper.getCurrentPos().getX();
		int hPosY = this.moveHelper.getCurrentPos().getY();
		int radius = this.stenchRadius;
		for (Map.Entry<Integer, Integer> id : percept.getWumpusStenchRadar().entrySet()) {

			// Go around the hunter and add the wumpus
			for (int y = hPosY - radius; y <= hPosY + radius; y++) {
				for (int x = hPosX - radius; x <= hPosX + radius; x++) {

					Tile t = getTile(x, y);
					int distance = Math.abs(hPosX - t.getPosX()) + Math.abs(hPosY - t.getPosY());

					if (distance <= radius)
						t.addWumpusId(id.getKey(), radius - distance);

					// Here is the wumpus with given distance/ stench
					// if(distance == id.getValue()) {
					//// t.addWumpusId(id.getKey(), id.getValue()); // TODO: not needed
					//
					// // Go around the possible wumpus tile and set stench values
					// // which is needed for the search
					// System.out.println("Have set wumpus set @" + x + "x und " + "y");
					//
					// for(int wY = t.getPosY() - radius; wY < t.getPosY() + radius; wY++) {
					// for(int wX = t.getPosX() - radius; wX < t.getPosX() + radius; wX++) {
					//
					//
					// Tile tW = getTile(wX, wY);
					// int manDistance = Math.abs(t.getPosX() - tW.getPosX())
					// + Math.abs(t.getPosY() - tW.getPosY());
					//
					// if(manDistance <= radius) {
					// tW.addWumpusId(id.getKey(), manDistance);
					// }
					// }
					// }
					// }
					// }
				}
			}
			System.out.println("HELLO " + getTile(18, 18).getWumpuse());
		}
	}

	private void showWumpusDebug() {
		for (int i = 0; i < view.size(); i++) {
			for (int j = 0; j < view.size(); j++) {

				Tile t = view.get(i).get(j);
				if (t != null) {
					Hashtable<Integer, Integer> w = view.get(i).get(j).getWumpuse();
					if (w != null && !w.isEmpty()) {
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

	/*
	 * Set the given type in tiles around the pos vector ignore a tile which have
	 * the already a counterpart
	 */
	public void setPossibleTypeAround(final TileType type) {
		int x = this.moveHelper.getCurrentPos().getX();
		int y = this.moveHelper.getCurrentPos().getY();

		// North
		if (!getTile(x, (y - 1)).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile(x, (y - 1)).addPossibleType(type);
		// East
		if (!getTile((x + 1), y).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile((x + 1), y).addPossibleType(type);
		// South
		if (!getTile(x, (y + 1)).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile(x, (y + 1)).addPossibleType(type);
		// West
		if (!getTile((x - 1), y).getPossibleTypes().contains(TileType.getCounterPart(type)))
			getTile((x - 1), y).addPossibleType(type);
	}

	/*
	 * Remove given type and set the counterpart PIT => NOT_PIT
	 */
	public void removePossibleTypeAround(final TileType type) {
		int x = this.moveHelper.getCurrentPos().getX();
		int y = this.moveHelper.getCurrentPos().getY();

		// North
		getTile(x, (y - 1)).removePossibleType(type);
		getTile(x, (y - 1)).addPossibleType(TileType.getCounterPart(type));
		// East
		getTile((x + 1), y).removePossibleType(type);
		getTile((x + 1), y).addPossibleType(TileType.getCounterPart(type));
		// South
		getTile(x, (y + 1)).removePossibleType(type);
		getTile(x, (y + 1)).addPossibleType(TileType.getCounterPart(type));
		// West
		getTile((x - 1), y).removePossibleType(type);
		getTile((x - 1), y).addPossibleType(TileType.getCounterPart(type));
	}

	public void bumpedIntoWall() {
		Tile n = null;
		Vector2 hunterPos = this.moveHelper.getCurrentPos();
		switch (this.moveHelper.getCurrentDir()) {
		case NORTH:
			n = getTile(hunterPos.getX(), hunterPos.getY() - 1);
			break;
		case EAST:
			n = getTile(hunterPos.getX() + 1, hunterPos.getY());
			break;
		case SOUTH:
			n = getTile(hunterPos.getX(), hunterPos.getY() + 1);
			break;
		case WEST:
			n = getTile(hunterPos.getX() - 1, hunterPos.getY());
			break;
		default:
			System.out.println("ERROR: Direction dosn't exist!");
		}
		n.setTileType(TileType.WALL);
	}

	public void bumpedIntoHunter() {
		// TODO: set hunter to pos
		// TODO: new search?
		// or other hunter bumped to this hunter?
	}

	public void movementSuccessful() {
		this.moveHelper.updatePos();

		getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);

		if (percept.isGlitter()) {
			System.out.println("DEBUG: GOLD found!");
			this.agent.setGoalGold(false);
			this.agent.setGoalGold(true);
			
			this.agent.newSearch();
			return;
		}

		if (this.moveHelper.getCurrentPos().equals(this.agent.getNextActionListPos().peek())) {
			this.agent.getNextActionListPos().pop();
			
			if (this.agent.getNextActionListPos().isEmpty()) {
				System.out.println("Search goal/location found!");
				
				if (this.agent.getGoalLocation() 
						&& this.moveHelper.getCurrentPos().equals(this.startInfo.getStartPosition())) {
					System.out.println("########### Game finished! ###########");
				} else {
					this.agent.newSearch();
				}
			}
		}
	}
	
	public void scream() {
		//TODO
	}
	
	public void breeze() {
		this.agent.newSearch();
	}

	public void wumpusKilled() {
		// TODO
	}
	
	public void noMoreShoots() {
		// TODO
	}
	
	public void glitter() {
		getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.GOLD);
		this.agent.setGoalGold(false);
		this.agent.setGoalLocation(true);
	}
	
	/*
	 * Return the tile where the hunter is standing
	 * 
	 * @return tile
	 */
	public Tile getHunterTile() {
		return getTile(this.moveHelper.getCurrentPos());
	}

	/*
	 * Get a tile from position (x, y), if there isn't a tile it creates a new one.
	 * 
	 * @return tile
	 */
	public Tile getTile(final int x, final int y) {

		Tile n = this.view.get(y).get(x);

		if (n == null) {
			n = new Tile(TileType.UNKNOWN, x, y);
			this.view.get(y).set(x, n);
		}
		return n;
	}

	/*
	 * Get a tile from position (Vector2 pos), if there isn't a tile it creates a
	 * new one.
	 * 
	 * Tile n = this.view.get(pos.getY()).get(pos.getX()); BUGGED why ever like now
	 * is better
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
	 * Reaturns a String with known tiles as a matrix Null tiles are shwon with a
	 * small "e" E = Empty W = Wall G = Gold P = Pit U = Unkown (unproven tiles)
	 * 
	 * TODO: NOT_XY what happens here?
	 */
	public String toStringKnown() {
		String s = "------------ Current View - Known --------------\n";
		s += "-  ";
		for (int i = 0; i < this.view.size(); i++) {
			s += i + " ";
		}
		s += "\n";
		for (int y = 0; y < this.view.size(); y++) {
			s += y + ". ";
			for (int x = 0; x < this.view.size(); x++) {
				Tile n = this.view.get(y).get(x);
				if (n != null) {
					s += n.getTileType().getSymbol();
					if (n.isBreeze())
						s += "B";
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
	 * Reaturns a String with possible and known tiles as a matrix e.g "(WPX)" (They
	 * are the UNKNOWN tiles) Null tiles are shwon with a small "e" E = Empty H =
	 * Hunter W = Wall G = Gold IDs numbers = Wumpus P = Pit B = Breeze
	 * 
	 * TODO: Brackets are currently bad
	 */
	public String toStringPossible() {
		String s = "------------ Current View - Possible --------------\n";
		s += "-  ";
		for (int i = 0; i < this.view.size(); i++) {
			s += i + "  ";
		}
		s += "\n";
		for (int y = 0; y < this.view.size(); y++) {
			s += y + ". ";
			for (int x = 0; x < this.view.size(); x++) {
				Tile n = this.view.get(y).get(x);
				if (n != null) {
					if (n.getTileType() == TileType.UNKNOWN) {
						s += "(";
						// if(n.getPossibleTypes() == null) continue; //TODO stuipd fix?
						// shouldnt be ther something if is it unknown?

						// Possible empty if there was a wumpus
						// if(n.getPossibleTypes() != null) {
						for (TileType t : n.getPossibleTypes()) {
							// if(t == null) break; //TODO need it by foreach?
							s += t.getSymbol();
						}
						s += ")";
						// }
					} else {
						s += " " + n.getTileType().getSymbol() + " ";
					}
					// --- Wumpus ---
					if (n.getWumpusIds() != null && !n.getWumpusIds().isEmpty()) {
						for (Integer i : n.getWumpusIds()) {
							s = s.substring(0, s.length() - 1);
							s += "X";
						}
					}
					// --- Wumpus END ---
					if (n.isBreeze()) {
						s = s.substring(0, s.length() - 1);
						s += "B";
					}
				} else {
					s += "e   ";
				}
				// --- Hunter ---
				if (x == this.moveHelper.getCurrentPos().getX() 
						&& y == this.moveHelper.getCurrentPos().getY()) {
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
