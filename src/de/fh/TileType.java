package de.fh;

public enum TileType {
	
	UNKNOWN('U'),
	EMPTY('E'),
	WALL('W'),
	NOT_WALL('w'),
	// TODO Remove? Moving things are not tile type
	//HUNTER('H'), 
	//WUMPUS('X'),
	PIT('P'),
	NOT_PIT('p'),
	GOLD('G');
	
	private final char symbol; 
	
	TileType(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() { return this.symbol; }
	
	// TODO: Must be not static? ref from this in Enum?
	public static boolean isCounterpart(final TileType typeOne, final TileType typeTwo) {
		if(typeOne == WALL && typeTwo == NOT_WALL) return true;
		if(typeOne == PIT && typeTwo == NOT_PIT) return true;
		return false;
	}
	
	/*
	 * @return the counterpart. If type hasn't counterpart give given type back
	 */
	public static TileType getCounterPart(final TileType type) {
		if(type == WALL) return TileType.NOT_WALL;
		if(type == NOT_WALL) return TileType.NOT_WALL;
		if(type == PIT) return TileType.NOT_PIT;
		if(type == NOT_PIT) return TileType.PIT;
		return type;
	}
}
