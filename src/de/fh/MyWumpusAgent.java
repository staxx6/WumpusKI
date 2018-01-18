package de.fh;


import de.fh.agent.WumpusHunterAgent;
import de.fh.game.Entity.Direction;
import de.fh.search.Goal;
import de.fh.search.GoalGold;
import de.fh.search.Node;
import de.fh.search.Search;
import de.fh.search.SearchValues;
import de.fh.util.Vector2;
import de.fh.viewui.ViewWindow;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

import java.util.Stack;


public class MyWumpusAgent extends WumpusHunterAgent {

	private State state;
	
	private HunterMoveHelper moveHelper;
	
	private boolean initDone = false;
	
	private Search search; 
	
//	private boolean wasTurn;
	private boolean wasScream;
	private boolean goalGold, goalKill;
	
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
//		this.percept = (HunterPercept) percept;

		if(actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			
			Vector2 startPos = new Vector2(18, 18);
			this.moveHelper = new HunterMoveHelper(new Vector2(startPos.getX(), startPos.getY()),
					startInfo.getAgentDirection());
			this.state = new State(startPos, this.startInfo, this.moveHelper.getCurrentPos());
			
			this.goalGold = true; // first goal
			this.goalKill = false;
			
			this.wasScream = false;
			
			this.initDone = true;
			
			this.nextActionListPos = new Stack<>();
			
			System.out.println("DEBUG: Game initialized");
		}

        if(actionEffect == HunterActionEffect.GAME_OVER) {
        	System.out.println("DEBUG: Game Over!");         	
        }

        if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	System.out.println("DEBUG: Bumped into WALL!");
        	Tile n = null;
        	Vector2 hunterPos = this.moveHelper.getCurrentPos();
        	// Only for debug - moves in circles
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
        	
        	System.out.println("-> Search goal/location NOT found - new search");
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
        	 
        	 if(this.moveHelper.getCurrentPos().equals(this.nextActionListPos.peek())) {
        		 this.nextActionListPos.pop();
        		 if(this.nextActionListPos.isEmpty()) {
        			 System.out.println("Search goal/location found!");
        			 newSearch();
        		 }
        	 }
        	 this.moveHelper.updatePos();
         }

         if(actionEffect == HunterActionEffect.GOLD_FOUND) {
        	 System.out.println("DEBUG: Collected GOLD!");
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
		
		System.out.println("ActionListPos: " + this.nextActionListPos);
		
		// if game init isn't done do NOTHING 
		if(!this.initDone) {
			System.out.println("--- END STEP (NO/SIT action) ---\n");
			return HunterAction.SIT; // TODO: Untested!
		}
		
		if(this.state.getHunterTile().getTileType() == TileType.GOLD) {
			// TODO: Well, do other stuff
			return HunterAction.GRAB;
		}
		
		if(this.wasScream) {
			// TODO
		}

		if(!this.nextActionListPos.isEmpty()) {
			this.moveHelper.moveTo(this.nextActionListPos.peek());
		} else {
			throw new IllegalStateException("ERROR: ActionList is empty! Should not be heppning here.");
		}
		
		/*
		newSearch(); // TODO: remove this one?
		
		
		// exit action() if it has to turn
		// TODO: possible problems if wumpus moved!!!
		// TODO: fix actionList pop()
		turnToNextDir(); // set isTurn
		if(this.isTurning) {
			System.out.println("Turning");
			return this.nextAction;
		} else {
//			if(this.hunterPos.equals(this.nextActionListPos.peek()) 
//					|| actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
//
//				// TODO: NIPPEWLPORDÄEAS
//				
//				this.nextActionListPos.clear();
//				System.out.println("CLEARD LIST");
//				this.search = null;
//				checkGoal();
//				goToNextTile(this.nextActionListPos.peek());
//				turnToNextDir();
//			}
			if(!this.nextActionListPos.isEmpty()) {
				goToNextTile(this.nextActionListPos.peek());
				newSearch();
				turnToNextDir();
			} else {
//				throw new IllegalStateException("ERROR: No action for this step!");
				this.search = null;
				newSearch();
			}
			
			if(!this.isTurning && this.search != null) {
				this.nextAction = HunterAction.GO_FORWARD;
				this.nextActionListPos.pop();
				System.out.println("No turning go forward");
			}
		}
		*/
		
		System.out.println("--- END STEP (action now SERVER) ---\n");
		return nextAction;
	}
	
	private void newSearch() {
		this.search = null;
		this.nextActionListPos.clear();
		
		if(this.goalGold) {
			this.search = new Search(new GoalGold(1), new SearchValues(), this.state);
			Node iNode = this.search.start(this.moveHelper.getCurrentPos());
			
			System.out.println("Hunter Pos is: " + this.moveHelper.getCurrentPos().toString());
			System.out.println("Search found something: " + iNode);
			
			this.nextActionListPos.clear();
			while(iNode != null) {
				this.nextActionListPos.push(iNode.getTile().getPosVector());
//				System.out.println("List added: " + iNode);
				iNode = iNode.getParentNode();
			}
//				this.nextActionListPos.pop(); // last one is root (hunterpos)
			System.out.println("List removed: " + this.nextActionListPos.pop());
			System.out.println("Created actionList: " + this.nextActionListPos);
		}
		
		if(this.goalKill) {
			// TODO
		}
	}
	
	/*
	 * Methode is only to move one tile!
	 * 
	 * Next tile can be only (should be...)  in one direction - 
	 * direct neighbour 
	 */
//	private void goToNextTile(final Vector2 pos) {
//		System.out.println("call goToNextTile methode");
//		if(this.hunterPos.equals(pos)) {
//			this.search = null;
//			System.out.println("hunterPos: " + this.hunterPos +
//					" and goToPos: " + pos + " - is equal -> return;");
//			return;
//		}
//		
//		Direction nextDir = this.hunterDir;
//		
//		System.out.print("Go to pos: " + pos + " ");
//		
//		if(this.hunterPos.getX() > pos.getX()) {
//			nextDir = Direction.WEST;
//			System.out.println("WEST");
//		} else if(this.hunterPos.getX() < pos.getX()) {
//			nextDir = Direction.EAST;
//			System.out.println("EAST");
//		}
//		
//		if(this.hunterPos.getY() > pos.getY()) {
//			nextDir = Direction.NORTH;
//			System.out.println("NORTH");
//		} else if(this.hunterPos.getY() < pos.getY()) {
//			nextDir = Direction.SOUTH;
//			System.out.println("SOUTH");
//		}
//		this.nextHunterDir = nextDir;
//	}
	
	//TODO: Untested!
	/*
	 * This methode set the isTurn booolean and nextDir direction
	 * 
	 * Turn fastest way, e.g. if Hunter is looking NORTH and needs to
	 * look WEST it should turn left and not right.
	 * 
	 * TODO: Possible better solution?
	 */
//	private void turnToNextDir() {
//		System.out.println("call turnToNextDir curr hunterDir: " + this.hunterDir);
//		if(this.hunterDir != this.nextHunterDir) {
//			System.out.print("!should be: " + this.nextHunterDir);
//			if(this.hunterDir == Direction.NORTH) {
//				if(this.nextHunterDir == Direction.WEST) {
//					this.nextAction = HunterAction.TURN_LEFT;
//					this.hunterDir = Direction.WEST;
//					System.out.println(" turn LEFT hunterDir set to WEST");
//				} else {
//					this.nextAction = HunterAction.TURN_RIGHT;
//					this.hunterDir = Direction.EAST;
//					System.out.println(" turn RIGHT hunterDir set to EAST");
//				}
//			} else if(this.hunterDir == Direction.EAST) {
//				if(this.nextHunterDir == Direction.NORTH) {
//					this.nextAction = HunterAction.TURN_LEFT;
//					this.hunterDir = Direction.NORTH;
//					System.out.println(" turn LEFT hunterDir set to NORTH");
//				} else {
//					this.nextAction = HunterAction.TURN_RIGHT;
//					this.hunterDir = Direction.SOUTH;
//					System.out.println(" turn RIGHT hunterDir set to SOUTH");
//				}
//			} else if(this.hunterDir == Direction.SOUTH) {
//				if(this.nextHunterDir == Direction.EAST) {
//					this.nextAction = HunterAction.TURN_LEFT;
//					this.hunterDir = Direction.EAST;
//					System.out.println(" turn LEFT hunterDir set to EAST");
//				} else {
//					this.nextAction = HunterAction.TURN_RIGHT;
//					this.hunterDir = Direction.WEST;
//					System.out.println(" turn RIGHT hunterDir set to WEST");
//				}
//			} else if(this.hunterDir == Direction.WEST) {
//				if(this.nextHunterDir == Direction.SOUTH) {
//					this.nextAction = HunterAction.TURN_LEFT;
//					this.hunterDir = Direction.SOUTH;
//					System.out.println(" turn LEFT hunterDir set to SOUTH");
//				} else {
//					this.nextAction = HunterAction.TURN_RIGHT;
//					this.hunterDir = Direction.NORTH;
//					System.out.println(" turn RIGHT hunterDir set to NORTH");
//				}
//			}
//			this.isTurning = true;
//			System.out.println("isTurning set to TRUE");
//		} else {
//			this.isTurning = false;
//			System.out.println("isTurning set to FALSE");
//		}
//	}
}