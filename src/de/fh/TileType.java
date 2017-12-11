package de.fh;

public enum TileType {
	
	UNKNOWN('U'),
	EMPTY('E'),
	WALL('W'),
	HUNTER('H'),
	WUMPUS('X'),
	PIT('P'),
	BREEZE('B'), // TODO: Not needed? @Node attr.
	GOLD('G');
	
	private final char symbol; 
	
	TileType(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() { return this.symbol; }
}
