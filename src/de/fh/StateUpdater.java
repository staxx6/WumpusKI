package de.fh;

import java.util.ArrayList;
import java.util.List;

import de.fh.util.Vector2;
import de.fh.wumpus.HunterPercept;

public class StateUpdater {
	private List<List<Node>> view;
	private HunterPercept percept;

	public StateUpdater(final List<List<Node>> view, final HunterPercept percept,
			final Vector2 startPos) {
		this.view = view;
		this.percept = percept;
		
		// TODO: Check Variables
		if(this.view == null) {
 			this.view = new ArrayList<>();
 			for(int y = 0; y < startPos.getY() * 2; y++) {
 				this.view.add(new ArrayList<>());
 				for(int x = 0; x < startPos.getX() * 2; x++) {
 					if(y == startPos.getY() && x == startPos.getX())
 						this.view.get(y).add((new Node(TileType.HUNTER, x, y)));
 					else
 						this.view.get(y).add((new Node(TileType.UNKNOWN, x, y)));
 				}
 			}
 		}
	}
	
	public void update(final Vector2 pos) {
		if(this.percept.isBump()) setPossibleTypeAround(pos, TileType.WALL);
	}
	
	private void setPossibleTypeAround(final Vector2 pos, final TileType type) {
		for(int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
			for(int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
				//TODO set type
				//TODO set value
			}
		}
	}
}
