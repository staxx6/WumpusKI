package de.fh.search;

public class SearchValues {
	
	private float wall;
	private float pit;
	private float wumpusDistanceFac;
	
	/*
	 * Create search values with standard values
	 */
	public SearchValues() {
		this.wall = 0.5f;
		this.pit = 10;
		this.wumpusDistanceFac = 10;
	}
	
	public SearchValues(final float wall, final float pit, 
			final float wumpusDistanceFac) {
		this.wall = wall;
		this.pit = pit;
		this.wumpusDistanceFac = wumpusDistanceFac;
	}
	
	public float getWall() {
		return wall;
	}

	public void setWall(float wall) {
		this.wall = wall;
	}

	public float getPit() {
		return pit;
	}

	public void setPit(float pit) {
		this.pit = pit;
	}

	public float getWumpusDistanceFac() {
		return wumpusDistanceFac;
	}

	public void setWumpusDistanceFac(float wumpusDistanceFac) {
		this.wumpusDistanceFac = wumpusDistanceFac;
	}
}
