package de.fh;

import de.fh.agent.WumpusHunterAgent;
import de.fh.game.Entity.Direction;
import de.fh.util.Vector2;
import de.fh.viewui.ViewWindow;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;
import javafx.application.Application;

import java.util.Hashtable;
import java.util.List;

/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
public class MyWumpusAgent extends WumpusHunterAgent {

	private HunterPercept percept;
	private HunterActionEffect actionEffect;
	//TODO Unused?
//	private Hashtable<Integer, Integer> stenchRadar;
	
	private List<List<Tile>> currView;	
	private State state;
	private Vector2 hunterPos;
	private Direction hunterDir;
	
	private boolean wasTurn;
	
	public static void main(String[] args) {
		
		// Start a new thread for the debug window
		new Thread(){
			@Override
			public void run() {
				javafx.application.Application.launch(ViewWindow.class);
			}
		}.start();
		ViewWindow viewWindow = ViewWindow.waitForViewWindow();
		
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

		this.percept = (HunterPercept) percept;

		if(actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			 // 
			Vector2 startPos = new Vector2(18, 18);
			this.hunterPos = new Vector2(startPos.getX(), startPos.getY());
			this.hunterDir = startInfo.getAgentDirection();
			this.wasTurn = false;
//			stenchRadar = percept.getWumpusStenchRadar();
			this.state = new State(this.currView, startPos, this.startInfo, this.hunterPos);
		}

        if(actionEffect == HunterActionEffect.GAME_OVER) {
        	System.out.println("DEBUG: Game Over!");         	
        }

        // Here must be the wall so set it in state
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
        }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
        	 //TODO: set hunter to pos
        	 //or other hunter bumped to this hunter?
        	 System.out.println("DEBUG: Bumped into hunter!");
         }

         if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
        	 System.out.println("DEBUG: Movement successful");
        	 
        	 // Ignore pos change if last update was just a turn
        	 //TODO: Wumpus moves\rumbles  here too?
        	 if(!this.wasTurn) {
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
        	 } else {
        		 // reset the turn bool
        		 this.wasTurn = false;
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
		
		stenchRadar = this.percept.getWumpusStenchRadar();
		
		System.out.println("update now");
		this.state.update(hunterPos, percept);
		
		this.actionEffect = actionEffect;
		System.out.println(this.state.toStringPossible());
		System.out.println("");
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
		
        if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			 this.nextAction = HunterAction.TURN_RIGHT;    
			 this.wasTurn = true;
			 switch(this.hunterDir) {
			 	case NORTH:
			 		this.hunterDir = Direction.EAST;
			 		break;
			 	case EAST:
			 		this.hunterDir = Direction.SOUTH;
			 		break;
			 	case SOUTH:
			 		this.hunterDir = Direction.WEST;
			 		break;
			 	case WEST:
			 		this.hunterDir = Direction.NORTH;
			 		break;
			 	default:
			 		System.out.println("ERROR: Direction dosn't exist?");
			     	 break;
			 }
        } else {
        	this.nextAction = HunterAction.GO_FORWARD;
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

		
		System.out.println("--- END STEP (action now) ---");
		return nextAction;
	}
}