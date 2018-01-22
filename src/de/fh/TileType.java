package de.fh;

public enum TileType {
	
	UNKNOWN('U'),
	EMPTY('E'),
	WALL('W'),
	NO_WALL('w'),
	PIT('P'),
	NO_PIT('p'),
	GOLD('G');
	
	private final char symbol; 
	
	TileType(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() { return this.symbol; }
	
	// TODO: Must be not static? ref from 'this' in Enum?
	public static boolean isCounterpart(final TileType typeOne, final TileType typeTwo) {
		if(typeOne == WALL && typeTwo == NO_WALL) return true;
		if(typeOne == PIT && typeTwo == NO_PIT) return true;
		return false;
	}
	
	/*
	 * @return the counterpart. If type hasn't a counterpart give given type back
	 */
	public static TileType getCounterPart(final TileType type) {
		if(type == WALL) return TileType.NO_WALL;
		if(type == NO_WALL) return TileType.NO_WALL;
		if(type == PIT) return TileType.NO_PIT;
		if(type == NO_PIT) return TileType.PIT;
		return type;
	}
}
