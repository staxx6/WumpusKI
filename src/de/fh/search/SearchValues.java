package de.fh.search;

public class SearchValues {
	
	private float wall;
	private float pit;
	private float wumpusDistanceFac;
	private float unknown;
	
	/*
	 * Create search values with standard values
	 */
	public SearchValues() {
		this.wall = 0.5f;
		this.pit = 10f;
		this.wumpusDistanceFac = 10f;
		this.unknown = 100f;
	}
	
	public SearchValues(final float wall, final float pit, 
			final float wumpusDistanceFac, final float unknown) {
		this.wall = wall;
		this.pit = pit;
		this.wumpusDistanceFac = wumpusDistanceFac;
		this.unknown = unknown;
	}
	
	public float getUnknown() {
		return this.unknown;
	}
	
	public void setUnknown(final float unknown) {
		this.unknown = unknown;
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
