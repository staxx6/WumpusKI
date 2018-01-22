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
	
	public static void main(String[] args) {
		
		// Start a new thread for the debug window
		/*
		new Thread(){
			@Override
			public void run() {
				javafx.application.Application.launch(ViewWindow.class);
			}
		}.start();
		ViewWindow viewWindow = ViewWindow.waitForViewWindow();
		*/
		
		MyWumpusAgent agent = new MyWumpusAgent("");
		MyWumpusAgent.start(agent,"127.0.0.1", 5000);
	}

	public MyWumpusAgent(String name) {
		super(name);
	}

	/**
	 * In dieser Methode kann das Wissen über die Welt (der State, der Zustand)
	 * entsprechend der aktuellen Wahrnehmungen anpasst, und die "interne Welt",
	 * die Wissensbasis, des Agenten kontinuierlich ausgebaut werden.
	 *
	 * Wichtig: Diese Methode wird aufgerufen, bevor der Agent handelt, d.h.
	 * bevor die action()-Methode aufgerufen wird...
	 *
	 * @param percept Aktuelle Wahrnehmung
	 * @param actionEffect Reaktion des Servers auf vorhergewählte Aktion
	 */
	@Override
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect) {
		System.out.println("--- START STEP (Update now) ---");
		
		if(actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			
			this.hunterStartPos = new Vector2(18, 18);
			this.moveHelper = new HunterMoveHelper(new Vector2(this.hunterStartPos.getX(), 
					this.hunterStartPos.getY()), startInfo.getAgentDirection());
			this.state = new State(this.hunterStartPos, this.startInfo, this.moveHelper.getCurrentPos());
	 		this.state.getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);
			
			this.goalGold = true; // first goal
			this.goalKill = false; // TODO: should be always true if there are still arrows
			this.goalLocation = false;
			
			this.wasScream = false;
			
			
			this.nextActionListPos = new Stack<>();
			
			this.initDone = true;
			System.out.println("DEBUG: Game initialized");
		}
		
        if(actionEffect == HunterActionEffect.GAME_OVER) {
        	System.out.println("DEBUG: Game Over!");         	
        }

        if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	System.out.println("DEBUG: Bumped into WALL!");
        	Tile n = null;
        	Vector2 hunterPos = this.moveHelper.getCurrentPos();
        	switch(this.moveHelper.getCurrentDir()) {
        		case NORTH:
        			n = this.state.getTile(
        					hunterPos.getX(), hunterPos.getY() - 1);
        			break;
        		case EAST:
        			n = this.state.getTile(
        					hunterPos.getX() + 1, hunterPos.getY());
        			break;
        		case SOUTH:
        			n = this.state.getTile(
        					hunterPos.getX(), hunterPos.getY() + 1);
        			break;
        		case WEST:
        			n = this.state.getTile(
        					hunterPos.getX() - 1, hunterPos.getY());
        			break;
        		default:
        			System.out.println("ERROR: Direction dosn't exist!");
        	}
        	n.setTileType(TileType.WALL);
        	
        	System.out.println("-> Search goal/location NOT found - new SEARCH: ");
        	newSearch();
        }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
        	 //TODO: set hunter to pos
        	 //TODO: new search?
        	 //or other hunter bumped to this hunter?
        	 System.out.println("DEBUG: Bumped into HUNTER!");
         }

         if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
        	 System.out.println("DEBUG: Movement SUCCESSFUL");
        	 
        	 this.moveHelper.updatePos();
        	 
        	 this.state.getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);
        	 
        	 if(percept.isGlitter()) {
        		 System.out.println("DEBUG: GOLD found!");
        		 this.goalGold = false;
        		 this.goalLocation = true;
        		 newSearch();
        	 }
        	 
        	 if(this.moveHelper.getCurrentPos().equals(this.nextActionListPos.peek())) {
        		 this.nextActionListPos.pop();
        		 if(this.nextActionListPos.isEmpty()) {
        			 System.out.println("Search goal/location found!");
        			 if(this.goalLocation && this.moveHelper.getCurrentPos().equals(this.hunterStartPos)) {
        				 System.out.println("########### Game finished! ###########");
        			 } else {
        				 newSearch();
        			 }
        		 }
        	 }
         }

     

         if(actionEffect == HunterActionEffect.WUMPUS_KILLED) {
        	 System.out.println("DEBUG: Wumpus KILLED!");
         }

         if(actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
        	 System.out.println("DEBUG: No more arrows!");
         }
         
         if(percept.isScream()) {
        	 this.wasScream = true;
         }
         
		System.out.println("DEBUG: Update now own state");
		this.state.update(this.moveHelper.getCurrentPos(), percept);
		
		// Set the pits around breeze is in state.class done
        if(percept.isBreeze()) {
        	newSearch();
        }
		
		if(this.search == null) {
			newSearch();
		}
		
		System.out.println(this.state.toStringPossible());
//		System.out.println(this.state.toStringKnown());
	}

	/**
	 * Diesen Part erweitern Sie so, dass die nächste(n) sinnvolle(n) Aktion(en),
	 * auf Basis der vorhandenen Zustandsinformationen und gegebenen Zielen, ausgeführt wird/werden.
	 * Der action-Part soll den Agenten so intelligent wie möglich handeln lassen
	 *
	 * Beispiel: Wenn die letzte Wahrnehmung
	 * "percept.isGlitter() == true" enthielt, ist "HunterAction.GRAB" eine
	 * geeignete Tätigkeit. Wenn Sie wissen, dass ein Quadrat "unsicher"
	 * ist, können Sie wegziehen
	 *
	 * @return Die nächste HunterAction die vom Server ausgeführt werden soll
	 */
	@Override
	public HunterAction action() {
		System.out.println("--- START action --- ActionListPos: " + this.nextActionListPos);
		
		// if game init isn't done do NOTHING 
		if(!this.initDone) {
			System.out.println("--- END STEP (action [SIT] now SERVER) init NOT done ---\n");
			return HunterAction.SIT;
		}
		
		if(this.state.getHunterTile().getTileType() == TileType.GOLD) {
			System.out.println("--- END STEP (action [GRAB] now SERVER) gold FOUND ---\n");
			this.state.getTile(this.moveHelper.getCurrentPos()).setTileType(TileType.EMPTY);
			return HunterAction.GRAB;
		}
		
		if(this.wasScream) {
			// TODO
		}

		if(!this.nextActionListPos.isEmpty()) {
			this.nextAction = this.moveHelper.moveTo(this.nextActionListPos.peek());
		} else {
			throw new IllegalStateException("ERROR: ActionList is empty! Should not be possible here "
					+ "OR Game finished!");
		}
		
		System.out.println("--- END STEP (action [" + nextAction + "] now SERVER) ---\n");
		return nextAction;
	}
	
	private void newSearch() {
		this.nextActionListPos.clear();
		
		Goal goal = null;
		SearchValues searchValues = null;
		
		if(this.goalGold) {
			System.out.println("# Start goalGold search:");
			goal = new GoalGold(9);
			searchValues = new SearchValues();
		}
		
		if(this.goalLocation) {
			System.out.println("# Start goalLocation search:");
			goal = new GoalLocation(this.hunterStartPos, 0, false);
			searchValues = new SearchValues();
		}
		
		if(this.goalKill) {
			// TODO
		}
		
		// ------ Search START -----
		this.search = new Search(goal, searchValues, this.state);
		Node iNode = this.search.start(this.moveHelper.getCurrentPos());
		System.out.println(" -> Search found something: " + iNode);
		
		
		while(iNode != null) {
			this.nextActionListPos.push(iNode.getTile().getPosVector());
			iNode = iNode.getParentNode();
		}
		this.nextActionListPos.pop(); // last one is root (hunterpos)
		System.out.println("-> Created actionList: " + this.nextActionListPos);
		// ------ Search END -----
	}
}