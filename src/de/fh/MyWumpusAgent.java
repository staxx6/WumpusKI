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

import java.util.List;
import java.util.Stack;


public class MyWumpusAgent extends WumpusHunterAgent {

//	private HunterPercept percept;
	private HunterActionEffect actionEffect;
	//TODO Unused?
//	private Hashtable<Integer, Integer> stenchRadar;
	
	private List<List<Tile>> currView;	// TODO: Remove
	private State state;
	private Vector2 hunterPos;
	private Direction hunterDir;
	
	private boolean initDone = false;
	
	private Goal currGoal;
	private Search search; 
	
//	private boolean wasTurn;
	private boolean wasScream;
	private boolean goalGold, goalLocation, goalKill;
	private Node goalNode;
	
	private Direction nextHunterDir;
	private boolean isTurning;
	private Stack<Vector2> nextActionListPos;
	
	
	public static void main(String[] args) {
		
//		if(new Vector2(1,2).equals(new Vector2(2, 2))) System.out.println("TRUE");
		
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
			this.hunterPos = new Vector2(startPos.getX(), startPos.getY());
			this.hunterDir = startInfo.getAgentDirection();
			this.nextHunterDir = this.hunterDir;
//			this.wasTurn = false;
//			stenchRadar = percept.getWumpusStenchRadar();
			this.state = new State(this.currView, startPos, this.startInfo, this.hunterPos);
			
			this.goalGold = true; // first goal
			this.goalLocation = false;
			this.goalKill = false;
			
			this.wasScream = false;
			
			this.initDone = true;
			
			this.isTurning = false;
			this.nextActionListPos = new Stack<>();
		}

        if(actionEffect == HunterActionEffect.GAME_OVER) {
        	System.out.println("DEBUG: Game Over!");         	
        }

        // Here must be the wall so set it in state
        // reset search!
        if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	System.out.println("DEBUG: Bumped into wall!");
        	Tile n = null;
        	// Only for debug - moves in circles
        	switch(this.hunterDir) {
        		case NORTH:
        			n = this.state.getTile(
        					this.hunterPos.getX(), this.hunterPos.getY() - 1);
        			break;
        		case EAST:
        			n = this.state.getTile(
        					this.hunterPos.getX() + 1, this.hunterPos.getY());
        			break;
        		case SOUTH:
        			n = this.state.getTile(
        					this.hunterPos.getX(), this.hunterPos.getY() + 1);
        			break;
        		case WEST:
        			n = this.state.getTile(
        					this.hunterPos.getX() - 1, this.hunterPos.getY());
        			break;
        		default:
        			System.out.println("ERROR: Direction dosn't exist!");
        	}
        	n.setTileType(TileType.WALL);
        	
        	search = null;
        }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
        	 //TODO: set hunter to pos
        	 //or other hunter bumped to this hunter?
        	 System.out.println("DEBUG: Bumped into hunter!");
         }

         if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
        	 System.out.println("DEBUG: Movement successful");
        	 
        	 // Ignore pos change if last update was just a turn
        	 // TODO: Wumpus moves\rumbles  here too?
        	 // TODO: wasTurn new var with isTurning
        	 if(!this.isTurning) {
        		 // Update Hunter postition
        		 switch(this.hunterDir) {
        		 case NORTH:
        			 this.hunterPos.setY(this.hunterPos.getY() - 1);
        			 break;
        		 case EAST:
        			 this.hunterPos.setX(this.hunterPos.getX() + 1);
        			 break;
        		 case SOUTH:
        			 this.hunterPos.setY(this.hunterPos.getY() + 1);
        			 break;
        		 case WEST:
        			 this.hunterPos.setX(this.hunterPos.getX() - 1);
        			 break;
        		 default:
        			 System.out.println("ERROR: Direction dosn't exist?");
        			 break;
        		 }
        	 }
         }

         if(actionEffect == HunterActionEffect.GOLD_FOUND) {
        	 System.out.println("DEBUG: Collected gold!");
         }

         if(actionEffect == HunterActionEffect.WUMPUS_KILLED) {
        	 System.out.println("DEBUG: Wumpus killed!");
         }

         if(actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
        	 System.out.println("DEBUG: No more arrows!");
         }
         
         if(percept.isScream()) {
        	 this.wasScream = true;
         }
         
//		stenchRadar = this.percept.getWumpusStenchRadar();
		
		System.out.println("DEBUG: Update now own state");
		this.state.update(hunterPos, percept);
		
		this.actionEffect = actionEffect;
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
		
		// if game init isn't done do NOTHING 
		if(!this.initDone) {
			System.out.println("--- END STEP (NO/SIT action) ---\n");
			return HunterAction.SIT; // TODO: Untested!
		}
		
		checkGoal();
		
		if(this.wasScream) {
			// TODO
		}
		
		// exit action() if it has to turn
		// TODO: possible problems if wumpus moved!!!
		turnToNextDir(); // End the turn ignore other actions
		if(this.isTurning) {
			System.out.println("Turning");
			return this.nextAction;
		} else {
			if(!this.nextActionListPos.isEmpty()) {
				goToNextTile(this.nextActionListPos.pop());
				turnToNextDir();
			} else {
//				throw new IllegalStateException("ERROR: No action for this step!");
				this.search = null;
				checkGoal();
			}
			
			if(!this.isTurning && this.search != null) {
				this.nextAction = HunterAction.GO_FORWARD;
				System.out.println("No turning go forward");
			}
		}
		
		
		if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
			
        }
		
		/*
		 * TODO: 1. Go to EAST till wall and then go down... With this we have
		 * 		 have a level border
		 * 		 2. Go to next dark tile if sure there is nothing
		 * 		 3. Go to next unsure dark tile with lowest posibility of bad things
		 * 			-> Wall > Pit > Wumpus
		 * 
		 * 		 - Shoot if sure where wumpus is
		 * 		 - Shoot if unsure and have enough arrows
		 * 			-> more arrows more risk shoots
		 * 		 - collect gold
		 *		 - Go away if stench to strong
		 *			-> but good shoot spot 
		 */
		
		System.out.println("--- END STEP (action now SERVER) ---\n");
		return nextAction;
	}
	
	private void checkGoal() {
		if(this.goalGold) {
			if(this.search == null) {
				this.search = new Search(new GoalGold(1), new SearchValues(), this.state);
				Node iNode = this.search.start(hunterPos);
				System.out.println("Hunter Pos is: " + this.hunterPos.toString());
				System.out.println("Search found something: " + iNode);		
				if(this.nextActionListPos == null) this.nextActionListPos = new Stack<>();
				while(iNode != null) {
					this.nextActionListPos.push(iNode.getTile().getPosVector());
					iNode = iNode.getParentNode();
				}
				this.nextActionListPos.pop(); // last one is root (hunterpos)
			}
		}
		
		if(this.goalKill) {
			// TODO
		}
	}
	
	/*
	 * Methode is only to move one tile!
	 */
	private void goToNextTile(final Vector2 pos) {
		System.out.println("call goToNextTile methode");
		if(this.hunterPos.equals(pos)) {
			System.out.println("hunterPos: " + this.hunterPos +
					" and goToPos: " + pos + " - is equal -> return;");
			return;
		}
		
		System.out.print("Go to pos: " + pos + " ");
		if(this.hunterPos.getX() > pos.getX()) {
			this.nextHunterDir = Direction.WEST;
			System.out.println("WEST");
			return; // move first x axis
		} else if(this.hunterPos.getX() < pos.getX()) {
			this.nextHunterDir = Direction.EAST;
			System.out.println("EAST");
			return; // move first x axis
		}
		
		if(this.hunterPos.getY() > pos.getY()) {
			this.nextHunterDir = Direction.NORTH;
			System.out.println("NORTH");
		} else if(this.hunterPos.getY() < pos.getY()) {
			this.nextHunterDir = Direction.SOUTH;
			System.out.println("SOUTH");
		}
	}
	
	//TODO: Untested!
	/*
	 * @return true if dir is wrong
	 */
	private void turnToNextDir() {
		if(this.hunterDir != this.nextHunterDir) {
			if(this.hunterDir == Direction.NORTH) {
				if(this.nextHunterDir == Direction.WEST) {
					this.nextAction = HunterAction.TURN_LEFT;
					this.hunterDir = Direction.WEST;
				} else {
					this.nextAction = HunterAction.TURN_RIGHT;
					this.hunterDir = Direction.EAST;
				}
			} else if(this.hunterDir == Direction.EAST) {
				if(this.nextHunterDir == Direction.NORTH) {
					this.nextAction = HunterAction.TURN_LEFT;
					this.hunterDir = Direction.NORTH;
				} else {
					this.nextAction = HunterAction.TURN_RIGHT;
					this.hunterDir = Direction.SOUTH;
				}
			} else if(this.hunterDir == Direction.SOUTH) {
				if(this.nextHunterDir == Direction.EAST) {
					this.nextAction = HunterAction.TURN_LEFT;
					this.hunterDir = Direction.EAST;
				} else {
					this.nextAction = HunterAction.TURN_RIGHT;
					this.hunterDir = Direction.WEST;
				}
			} else if(this.hunterDir == Direction.WEST) {
				if(this.nextHunterDir == Direction.SOUTH) {
					this.nextAction = HunterAction.TURN_LEFT;
					this.hunterDir = Direction.SOUTH;
				} else {
					this.nextAction = HunterAction.TURN_RIGHT;
					this.hunterDir = Direction.NORTH;
				}
			}
			this.isTurning = true;
			System.out.println("isTurning set to TRUE");
		} else {
			this.isTurning = false;
			System.out.println("isTurning set to FALSE");
		}
	}
}