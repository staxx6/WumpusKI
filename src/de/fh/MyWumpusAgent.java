package de.fh;

import de.fh.agent.WumpusHunterAgent;
import de.fh.search.Goal;
import de.fh.search.GoalGold;
import de.fh.search.GoalLocation;
import de.fh.search.Node;
import de.fh.search.Search;
import de.fh.search.SearchValues;
import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

import java.util.Stack;

public class MyWumpusAgent extends WumpusHunterAgent {

	private State state;
	private Vector2 hunterStartPos;

	private HunterMoveHelper moveHelper;

	private boolean initDone = false;

	private Search search;

	private boolean wasScream;
	private boolean goalGold, goalKill, goalLocation;

	private Stack<Vector2> nextActionListPos;

	private boolean quitGame;

	public static void main(String[] args) {

		// Start a new thread for the debug window
		/*
		 * new Thread(){
		 * 
		 * @Override public void run() {
		 * javafx.application.Application.launch(ViewWindow.class); } }.start();
		 * ViewWindow viewWindow = ViewWindow.waitForViewWindow();
		 */

		MyWumpusAgent agent = new MyWumpusAgent("");
		MyWumpusAgent.start(agent, "127.0.0.1", 5000);
	}

	public MyWumpusAgent(String name) {
		super(name);
	}

	/**
	 * In dieser Methode kann das Wissen über die Welt (der State, der Zustand)
	 * entsprechend der aktuellen Wahrnehmungen anpasst, und die "interne Welt", die
	 * Wissensbasis, des Agenten kontinuierlich ausgebaut werden.
	 *
	 * Wichtig: Diese Methode wird aufgerufen, bevor der Agent handelt, d.h. bevor
	 * die action()-Methode aufgerufen wird...
	 *
	 * @param percept
	 *            Aktuelle Wahrnehmung
	 * @param actionEffect
	 *            Reaktion des Servers auf vorhergewählte Aktion
	 */
	@Override
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect) {
		System.out.println("--- START STEP (Update now) ---");

		if (actionEffect == HunterActionEffect.GAME_INITIALIZED) {

			this.hunterStartPos = new Vector2(18, 18);
			this.moveHelper = new HunterMoveHelper(new Vector2(this.hunterStartPos.getX(), this.hunterStartPos.getY()),
					startInfo.getAgentDirection());
			this.state = new State(this.hunterStartPos, this.startInfo, this.moveHelper, percept, this);
			this.state.getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);

			this.goalGold = true; // first goal
			this.goalKill = false; // TODO: should be always true if there are still arrows
			this.goalLocation = false; // last goal (go back to start position)

			this.wasScream = false;
			this.quitGame = false;

			this.nextActionListPos = new Stack<>();

			this.initDone = true;
			System.out.println("DEBUG: Game initialized");
		}

		// -------- Server response --------

		if (actionEffect == HunterActionEffect.GAME_OVER) {
			System.out.println("DEBUG: Game Over!");
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			System.out.println("DEBUG: Bumped into WALL!");
			this.state.bumpedIntoWall();
			System.out.println("-> Search goal/location NOT found - new SEARCH: ");
			newSearch();
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
			System.out.println("DEBUG: Bumped into HUNTER!");
			this.state.bumpedIntoHunter();
		}

		if (actionEffect == HunterActionEffect.WUMPUS_KILLED) {
			System.out.println("DEBUG: Wumpus KILLED!");
			this.state.wumpusKilled();
		}

		if (actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
			System.out.println("DEBUG: No more arrows!");
			this.state.noMoreShoots();
		}
		
		if (actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
			System.out.println("DEBUG: Movement SUCCESSFUL");
			this.state.movementSuccessful();
		}

		// -------- Server response END --------

		// -------- Get Percept stuff --------

		if (percept.isBump()) {
			this.state.setPossibleTypeAround(TileType.WALL);
		} else {
			this.state.removePossibleTypeAround(TileType.WALL);
		}

		if (percept.isBreeze()) {
			this.state.setPossibleTypeAround(TileType.PIT);
			this.state.getTile(this.moveHelper.getCurrentPos()).setBreeze(true);
			newSearch();
		} else {
			this.state.removePossibleTypeAround(TileType.PIT);
		}

		if (percept.isGlitter()) {
			System.out.println("GOLD found!");
			this.state.glitter();
			newSearch();
		}

		if (percept.getWumpusStenchRadar().isEmpty()) {
			this.state.removeWumpusAll();
			newSearch();
		} else {
			if(!this.state.getHunterTile().getWumpuse().isEmpty()) {
				newSearch();
			}
			this.state.removeWumpusAll();
			this.state.setWumpusStench();
		}

		// TODO: Is it ever triggered?
		if (this.search == null) {
			newSearch();
		}

		// -------- Get Percept stuff END --------

		System.out.println(this.state.toStringPossible());
		// System.out.println(this.state.toStringKnown());
	}

	/**
	 * Diesen Part erweitern Sie so, dass die nächste(n) sinnvolle(n) Aktion(en),
	 * auf Basis der vorhandenen Zustandsinformationen und gegebenen Zielen,
	 * ausgeführt wird/werden. Der action-Part soll den Agenten so intelligent wie
	 * möglich handeln lassen
	 *
	 * Beispiel: Wenn die letzte Wahrnehmung "percept.isGlitter() == true" enthielt,
	 * ist "HunterAction.GRAB" eine geeignete Tätigkeit. Wenn Sie wissen, dass ein
	 * Quadrat "unsicher" ist, können Sie wegziehen
	 *
	 * @return Die nächste HunterAction die vom Server ausgeführt werden soll
	 */
	@Override
	public HunterAction action() {
		System.out.println("--- START action --- ActionListPos: " + this.nextActionListPos);

		if (this.quitGame) {
			System.out.println("--- END STEP (action [QUIT] now SERVER) search found nothing ---\n");
			return HunterAction.QUIT_GAME;
		}

		if (!this.initDone) {
			System.out.println("--- END STEP (action [SIT] now SERVER) init NOT done ---\n");
			return HunterAction.SIT;
		}

		if (this.state.getHunterTile().getTileType() == TileType.GOLD) {
			System.out.println("--- END STEP (action [GRAB] now SERVER) gold FOUND ---\n");
			this.state.getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);
			return HunterAction.GRAB;
		}

		if (this.wasScream) {
			// TODO
		}

		if (!this.nextActionListPos.isEmpty()) {
			this.nextAction = this.moveHelper.moveTo(this.nextActionListPos.peek());
		} else {
			System.out.println("--- END STEP (action [QUIT] now SERVER) search found nothing ---\n");
			return HunterAction.QUIT_GAME;
		}

		System.out.println("--- END STEP (action [" + nextAction + "] now SERVER) ---\n");
		return nextAction;
	}

	public void newSearch() {
		if(this.quitGame) return;
		
		System.out.println("Issued new search: gold: " + this.goalGold + " loc: " + this.goalLocation);
		this.nextActionListPos.clear();

		Goal goal = null;
		SearchValues searchValues = null;

		if (this.goalGold) {
			System.out.println("# Start goalGold search:");
			goal = new GoalGold(5.5f);
			searchValues = new SearchValues();
		}

		if (this.goalLocation) {
			System.out.println("# Start goalLocation search:");
			goal = new GoalLocation(this.hunterStartPos, 9, false);
			searchValues = new SearchValues();
		}

		if (this.goalKill) {
			// TODO
		}

		// ------ Search START -----
		this.search = new Search(goal, searchValues, this.state);
		Node iNode = this.search.start(this.moveHelper.getCurrentPos());
		if (iNode == null) {
			this.quitGame = true;
			return;
		}
		System.out.println(" -> Search found something: " + iNode);

		while (iNode != null) {
			this.nextActionListPos.push(iNode.getTile().getPosVector());
			iNode = iNode.getParentNode();
		}
		this.nextActionListPos.pop(); // last one is root (hunterpos)
		System.out.println("-> Created actionList: " + this.nextActionListPos);
		// ------ Search END -----
	}

	public void setGoalGold(final boolean goalGold) {
		this.goalGold = goalGold;
	}

	public boolean getGoalLocation() {
		return this.goalLocation;
	}

	public void setGoalLocation(final boolean goalLocation) {
		this.goalLocation = goalLocation;
	}

	public void setQuitGame(final boolean quitGame) {
		this.quitGame = quitGame;
	}

	public Stack<Vector2> getNextActionListPos() {
		return this.nextActionListPos;
	}
}
