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
 			for(int i = 0; i < startPos.getY() * 2; i++) {
 				this.view.add(new ArrayList<>());
 				for(int j = 0; j < startPos.getY() * 2; j++) {
 					if(i == startPos.getY() && j == startPos.getX())
 						this.view.get(i).add((new Node(TileType.HUNTER, j, i)));
 					else
 						this.view.get(i).add((new Node(TileType.UNKNOWN, j, i)));
 				}
 			}
 		}
	}
	
	public void update(final Vector2 pos) {
		if(this.percept.isBump()) setPossibleWallAround(pos);
	}
	
	private void setPossibleWallAround(final Vector2 pos) {
		for(int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
			for(int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
				//TODO
			}
		}
	}
}
