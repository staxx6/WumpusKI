package de.fh;

public enum TileType {
	
	UNKNOWN('U'),
	EMPTY('E'),
	WALL('W'),
	HUNTER('H'),
	WUMPUS('X'),
	PIT('P'),
	STENCH('S'),
	BREEZE('B'),
	GOLD('G');
	
	private final char symbol; 
	
	TileType(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() { return this.symbol; }
}
