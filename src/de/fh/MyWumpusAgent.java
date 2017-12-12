package de.fh;

import de.fh.agent.WumpusHunterAgent;
import de.fh.game.Entity.Direction;
import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

import java.util.Hashtable;
import java.util.List;

/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
public class MyWumpusAgent extends WumpusHunterAgent {

	private HunterPercept percept;
	private HunterActionEffect actionEffect;
	private Hashtable<Integer, Integer> stenchRadar;
	
	private List<List<Node>> currView;	
	private StateUpdater stateUpdater;
	private Vector2 hunterPos;
	
	public static void main(String[] args) {

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

		/**
		 * Je nach Sichtbarkeit & Schwierigkeitsgrad (laut Serverkonfiguration)
		 * aktuelle Wahrnehmung des Hunters.
		 * Beim Wumpus erhalten Sie je nach Level mehr oder weniger Mapinformationen.
		 */
		this.percept = (HunterPercept) percept;

//         Aktuelle Reaktion des Server auf die letzte übermittelte Action.

         if(actionEffect == HunterActionEffect.GAME_INITIALIZED) {
        	Vector2 startPos = new Vector2(18, 18);
     		this.hunterPos = new Vector2(startPos.getX(), startPos.getY()); 
     		stenchRadar = percept.getWumpusStenchRadar();
     		this.stateUpdater = new StateUpdater(this.currView, 
     				startPos, this.stenchRadar, this.startInfo);
         }

         if(actionEffect == HunterActionEffect.GAME_OVER) {
        	 System.out.println("DEBUG: Game Over!");         	
         }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	 System.out.println("DEBUG: Bumped into wall!");
         }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
        	 System.out.println("DEBUG: Bumped into hunter!");
         }

         if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
        	 
        	 // Update Hunter postition
        	 switch(startInfo.getAgentDirection()) {
        	 	case NORTH:
        	 		hunterPos.setY(hunterPos.getY() - 1);
        	 		break;
        	 	case EAST:
        	 		hunterPos.setX(hunterPos.getX() + 1);
        	 		break;
        	 	case SOUTH:
        	 		hunterPos.setY(hunterPos.getY() + 1);
        	 		break;
        	 	case WEST:
        	 		hunterPos.setX(hunterPos.getX() - 1);
        	 		break;
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

		System.out.println("-------------------------------");
		this.actionEffect = actionEffect;
		System.out.println(actionEffect.toString()+ "\n");

		/*
		percept.isBreeze();
        percept.isBump();
        percept.isGlitter(); Angrenzende Wand
        percept.isRumble(); Wumpus move
        percept.isScream();
        percept.isStench();
        percept.getWumpusStenchRadar()
        */

		/*
        percept.getWumpusStenchRadar() enthält alle Wumpi in max. R(ie)eichweite in einer Hashtable.
        Jeder Wumpi besitzt eine unique WumpusID (getKey).
        Die Manhattendistanz zum jeweiligen Wumpi ergibt sich aus der Gestanksitensität (getValue).
		*/
		
		// --------------------------------------------------------------
		
		stenchRadar = this.percept.getWumpusStenchRadar();
		this.stateUpdater.update(hunterPos, percept);
		
		// --------------------------------------------------------------
		
		/*

		//Gebe alle riechbaren Wumpis aus
		System.out.println("WumpusID: Intensitaet");
		if(stenchRadar.isEmpty())
		{
			System.out.println("Kein Wumpi zu riechen");
		}
		for(Map.Entry<Integer, Integer> g : stenchRadar.entrySet()){
			System.out.println(g.getKey() + ":\t\t" + g.getValue() );
		}
		 */
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

		/*HunterAction
        Mögliche HunterActions sind möglich:

       	HunterAction.GO_FORWARD
       	HunterAction.TURN_LEFT
		HunterAction.TURN_RIGHT
		HunterAction.SHOOT
		HunterAction.SIT
		HunterAction.GRAB
		HunterAction.QUIT_GAME
		*/

		nextAction = HunterAction.GO_FORWARD;
		System.out.println("nextAction: "+nextAction);
		return nextAction;
	}
}