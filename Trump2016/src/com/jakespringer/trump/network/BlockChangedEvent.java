package com.jakespringer.trump.network;

import com.jakespringer.trump.game.Walls;
import com.jakespringer.trump.game.Tile.WallType;

public class BlockChangedEvent implements GameEvent {

	public int x;
	public int y;
	public String wallType;
	public String image;
	
	public BlockChangedEvent(int _x, int _y, String _wallType, String _image) {
		x = _x;
		y = _y;
		wallType = _wallType;
		image = _image;
	}
	
	@Override
	public String toMessage() {
		return "BLOCKCHANGED" + " " + x + " " + y + " " + wallType + " " + image;
	}
	
	public static void handle(String... args) {
		int nx = Integer.parseInt(args[0]);
		int ny = Integer.parseInt(args[1]);
		Walls.walls.grid[nx][ny].change(WallType.valueOf(args[2]), args[3]);
	}
}
