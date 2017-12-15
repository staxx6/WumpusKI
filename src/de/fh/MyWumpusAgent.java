package de.fh;

import de.fh.agent.WumpusHunterAgent;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
public class MyWumpusAgent extends WumpusHunterAgent {

	private HunterPercept percept;
	private HunterActionEffect actionEffect;
	
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

		this.percept = (HunterPercept) percept;

		if(actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			System.out.println("DEBUG: Game initialized");
		}

        if(actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	System.out.println("DEBUG: Bumped into wall!");
        }

         if(actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
        	 System.out.println("DEBUG: Bumped into hunter!");
         }

         if(actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
        	 System.out.println("DEBUG: Movement successful");
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
         
		this.actionEffect = actionEffect;
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
		System.out.println("ActionEffect is now: " + this.actionEffect);
        if(this.actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
        	// Turn hunters direction
			 this.nextAction = HunterAction.TURN_RIGHT;    
			 System.out.println("NextAction set turn right");
        } else {
        	this.nextAction = HunterAction.GO_FORWARD;
        	System.out.println("NextAction set to go forward");
        }
		System.out.println("--- END STEP (action now) ---");
		return nextAction;
	}
}