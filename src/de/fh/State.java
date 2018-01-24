package de.fh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import de.fh.search.Goal.Goals;
import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.WumpusStartInfo;

/*
 * TODO: No growing List! IndexOutOfBoundsException possible by getNode!!! 
 * TODO: Fix wumpus radar
 */

public class State {

	private Vector2 startPos;
	private int stenchRadius;

	private List<List<Tile>> view;
	private Vector2 currViewSizeLimit; // (x, y)
	private HunterMoveHelper moveHelper;

	private HunterPercept percept;
	private MyWumpusAgent agent;

	private HashMap<Vector2, Float> historyStench;
	private boolean isInStenchRadius;

	private int arrows;

	public State(final Vector2 startPos, final WumpusStartInfo startInfo, final HunterMoveHelper moveHelper,
			final HunterPercept percept, final MyWumpusAgent agent) {
		this.moveHelper = moveHelper;
		this.percept = percept;
		this.agent = agent;
		this.startPos = startPos;
		this.historyStench = new HashMap<>();
		this.arrows = startInfo.getShots();
		this.isInStenchRadius = false;

		this.stenchRadius = startInfo.getStenchDistance();

		// Create Lists and fill it with null
		this.view = new ArrayList<>();
		for (int y = 0; y < this.startPos.getY() * 2; y++) {
			this.view.add(new ArrayList<>());
			for (int x = 0; x < this.startPos.getX() * 2; x++) {
				this.view.get(y).add(null);
			}
		}
		this.currViewSizeLimit = new Vector2(this.view.get(0).size(), this.view.size());
	}
	
	public void updatePercept(final HunterPercept percept) {
		this.percept = percept;
		if(this.percept.getWumpusStenchRadar().isEmpty()) {
			this.isInStenchRadius = false; 
		} else {
			this.isInStenchRadius = true;
		}
	}
	
	public boolean isInStenchRadius() {
		return this.isInStenchRadius;
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
		Vector2 hPos = this.moveHelper.getCurrentPos();
		int hPosX = hPos.getX();
		int hPosY = hPos.getY();

		System.out.println("set stench: " + percept.getWumpusStenchRadar());
		for (Map.Entry<Integer, Integer> id : percept.getWumpusStenchRadar().entrySet()) {

			// TODO: Untested

			// --------- history stuff ----------
			if (this.historyStench.get(hPos) != null) {
				if(this.historyStench.get(hPos) < id.getValue())
				this.historyStench.put(new Vector2(this.moveHelper.getCurrentPos().getX(),
						this.moveHelper.getCurrentPos().getY()), (float) id.getValue());
			}
			// --------- history stuff END ----------

			// Go around the hunter and add the wumpus stench
			for (int y = hPosY - id.getValue(); y <= hPosY + id.getValue(); y++) {
				for (int x = hPosX - id.getValue(); x <= hPosX + id.getValue(); x++) {

					Tile t = getTile(x, y);
					int distance = Math.abs(hPosX - t.getPosX()) + Math.abs(hPosY - t.getPosY());

					if (distance <= id.getValue()) {
						float wumpusRisk = id.getValue() - distance;
						if (wumpusRisk != 0) {
							t.addWumpusId(id.getKey(), wumpusRisk);
						}
					}
				}
			}
		}
		 showWumpusDebug();
	}

	private void showWumpusDebug() {
		for (int y = 0; y < view.size(); y++) {
			for (int x = 0; x < view.size(); x++) {

				Tile t = view.get(y).get(x);
				if (t != null) {
					Hashtable<Integer, Float> w = t.getWumpuse();
					if (w != null && !w.isEmpty()) {
						System.out.print(x + "," + y + t.getWumpuse() + " ");
					} else {
						System.out.print("              ");
					}
				} else {
					System.out.print("            ");
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

		if (!this.agent.getNextActionListPos().isEmpty() && this.moveHelper.getCurrentPos().equals(this.agent.getNextActionListPos().peek())) {
			this.agent.getNextActionListPos().pop();

			if (this.agent.getNextActionListPos().isEmpty()) {
				System.out.print("Search goal/location found! Check for end goalLoc: ");
				if (this.agent.getGoals().contains(Goals.LOCATION)
						&& this.moveHelper.getCurrentPos().equals(this.startPos)) {
					System.out.println("########### Game finished! ###########");
					this.agent.setQuitGame(true);
				} else {
					this.agent.setTriggerNewSearch(true);
					System.out.println("\n trigger by effect move");
				}

				if (this.agent.getGoals().contains(Goals.GO_BACK)) {
					System.out.println("Go back goal found!");
					this.agent.removeGoalGoBack();
					this.agent.setTriggerNewSearch(true);
					System.out.println("\n trigger by go back found");
				}
			}
		}

		System.out.print("Try shoot: ");
		if (hasArrows() && !this.agent.wasShoot()) {
			Tile checkTile = getTile(this.moveHelper.getNextPos());
			if (checkTile.getTileType() != TileType.WALL || checkTile.getTileType() != TileType.PIT) {
				System.out.print("no walls/pits " + this.percept.getWumpusStenchRadar());
				for (Map.Entry<Integer, Integer> id : percept.getWumpusStenchRadar().entrySet()) {
					if (id.getValue() < 3) {
						this.agent.setShootNow(true);
//						System.out.println("Set shoot true");
					} else {
//						System.out.println("not under 3");
					}
				}
				System.out.println("done shoot or nothin done");
			} else {
//				System.out.println("wall");
			}
		} else {
			System.out.println("no arrows");
		}
	}

	// TODO: Untested
	public void rumble() {
		this.historyStench.replaceAll((v, f) -> {
			if (f < 1) {
				f = 0f;
			} else {
				f /= 0.5f;
			}
			return f;
		});

		for (Vector2 v : historyStench.keySet()) {
			if (this.historyStench.get(v) == 0)
				this.historyStench.remove(v);
		}
	}

	// Percept scream and server effect killed?
	public void scream() {
		// TODO all? fix only for one
		// this.agent.getGoals().remove(Goals.KILL);
	}

	public void breeze() {
		this.agent.setTriggerNewSearch(true);
		System.out.println("trigger by effect breeze @ state");
	}

	// Percept scream and server effect killed?
	public void wumpusKilled() {
		// TODO all? fix only for one
		this.agent.getGoals().remove(Goals.KILL);
	}

	public void noMoreShoots() {
		this.agent.getGoals().remove(Goals.KILL);
	}

	public void glitter() {
		getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.GOLD);
		this.agent.getGoals().add(Goals.LOCATION);
		this.agent.getGoals().remove(Goals.GOLD);
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

	public float getHistoryStench(final Vector2 pos) {
		if (this.historyStench.get(pos) == null) {
			return 0;
		} else {
			return this.historyStench.get(pos);
		}
	}

	public boolean hasArrows() {
		if (this.arrows > 0)
			return true;
		else
			return false;
	}

	public void arrowShot() {
		this.arrows -= 1;
		// TODO
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
		s += "-\t";
		for (int i = 17; i < this.view.size() - 7; i++) {
			s += i + "\t";
		}
		s += "\n";
		for (int y = 17; y < this.view.size() - 7; y++) {
			s += y + "\t";
			for (int x = 17; x < this.view.size() - 7; x++) {
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
						s += n.getTileType().getSymbol();
					}
					// --- Wumpus ---
					if (n.getWumpusIds() != null && !n.getWumpusIds().isEmpty()) {
						// for (Integer i : n.getWumpusIds()) {
						s = s.substring(0, s.length() - 1);
						s += "X";
						// }
					}
					// --- Wumpus END ---
					if (n.isBreeze()) {
						s = s.substring(0, s.length() - 1);
						s += "B";
					}
				} else {
					s += "e";
				}
				// --- Hunter ---
				if (x == this.moveHelper.getCurrentPos().getX() && y == this.moveHelper.getCurrentPos().getY()) {
					s = s.substring(0, s.length() - 1);
					s += "H";
				}
				s += "\t";
				// --- Hunter END ---
			}
			s += "\n";
		}
		s += "----------------------------------------------";
		return s;
	}
}
