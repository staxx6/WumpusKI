package de.fh;

public enum TileType {
	
	UNKNOWN('U'),
	EMPTY('E'),
	WALL('W'),
	// TODO Remove? Moving things are not tile type
	//HUNTER('H'), 
	//WUMPUS('X'),
	PIT('P'),
	GOLD('G');
	
	private final char symbol; 
	
	TileType(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() { return this.symbol; }
}
