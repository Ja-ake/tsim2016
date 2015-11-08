package com.jakespringer.trump.ui;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Camera;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Texture;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;
import com.jakespringer.trump.game.Tile;
import com.jakespringer.trump.game.Tile.WallType;
import com.jakespringer.trump.game.Walls;


public class BuildMenu extends AbstractEntity {

	private final Vec2 position;
	private final Vec2 size;
	private Color4 color;
	private Texture texture;
	private int numWood = 128;
	private boolean canBuild = false;
	private boolean buildSelection = false;
	private boolean redSide;
	
//	private Signal<Vec2> displayPosition;
//	private Signal<Vec2> displaySize;
	
	public BuildMenu(boolean red) {
		position = new Vec2(33, 32);
		size = new Vec2(4, 4);
		color = Color4.BLUE;
		redSide = red;
	}
	
	@Override
	public void create() {
		texture = SpriteContainer.loadSprite("wood");
//		FontContainer.add("default", "arial", Font.PLAIN, 32);
		
		add(Input.whenMouse(0, true).forEach(() -> {
			if (Mouse.getX() == 0) {
				
			}
		}));
		
		add(Reagan.continuous.forEach(dt -> {
			Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
			Graphics2D.drawSprite(texture, position, size, 0, Color4.WHITE);
			Graphics2D.drawRect(position.subtract(size.multiply(8)), size.multiply(16), color);
			Graphics2D.drawText("" + numWood, "Default", position.subtract(size.withX(0).multiply(4)), Color.white);
			Camera.setProjection2D(Window.LL(), Window.UR());
			
			Vec2 mpf = Walls.walls.snapToGrid(Input.mouseWorldPosition.get());
			int gridx = (int) (mpf.x/Walls.walls.wallSize);
			int gridy = (int) (mpf.y/Walls.walls.wallSize);
						
			if (buildSelection == true) {
				try {
//					System.out.println("hi: " + (Walls.walls.grid[gridx+1][gridy].type == Tile.WallType.AIR
//							&& Walls.walls.grid[gridx-1][gridy].type == Tile.WallType.AIR
//							&& Walls.walls.grid[gridx][gridy+1].type == Tile.WallType.AIR
//							&& Walls.walls.grid[gridx][gridy-1].type == Tile.WallType.AIR));
					System.out.println(Robot.redList.get(0).position.get());

					if ((!redSide)?(Walls.walls.zoneControl[Walls.walls.grid[gridx][gridy].zone-1] < -0.5):(Walls.walls.zoneControl[Walls.walls.grid[gridx][gridy].zone-1] > 0.5) 
							&& (!(Walls.walls.grid[gridx+1][gridy].type == Tile.WallType.AIR
							&& Walls.walls.grid[gridx-1][gridy].type == Tile.WallType.AIR
							&& Walls.walls.grid[gridx][gridy+1].type == Tile.WallType.AIR
							&& Walls.walls.grid[gridx][gridy-1].type == Tile.WallType.AIR
							|| Walls.walls.grid[gridx][gridy].type == Tile.WallType.BACKGROUND))
							&& ((!Robot.blueList.stream().anyMatch(r -> 
								Walls.collideAABB(r.position.get(), Robot.size, mpf.add(new Vec2(1, 1).multiply(Walls.walls.wallSize/2)), 
										new Vec2(Walls.walls.wallSize, Walls.walls.wallSize))))
							&& (!Robot.redList.stream().anyMatch(r -> 
								Walls.collideAABB(r.position.get(), Robot.size, mpf.add(new Vec2(1, 1).multiply(Walls.walls.wallSize/2)), 
										new Vec2(18, 18)))))) {
						Graphics2D.fillRect(mpf, new Vec2(36, 36), Color4.GREEN);
						canBuild = true;
					} else {
						Graphics2D.fillRect(mpf, new Vec2(36, 36), Color4.RED);
						canBuild = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}));
		
		add(Input.whenMouse(0, true).forEach(() -> {
			Vec2 pos = Input.getMouseScreen();

			int gridx = (int) Walls.walls.snapToGrid(Input.getMouse()).divide(Walls.walls.wallSize).x;
			int gridy = (int) Walls.walls.snapToGrid(Input.getMouse()).divide(Walls.walls.wallSize).y;
			
			if (pos.x > position.subtract(size.multiply(8)).x
					&& pos.y > position.subtract(size.multiply(8)).y
					&& pos.x < position.subtract(size.multiply(8)).add(size.multiply(16)).x
					&& pos.y < position.subtract(size.multiply(8)).add(size.multiply(16)).y) {
				color = color.equals(Color4.BLUE) ? Color4.RED : Color4.BLUE;
				buildSelection = !buildSelection;
			} else if (canBuild == true && buildSelection == true && numWood > 0) {
				System.out.println(gridx + " " + gridy);
				int oldzone = Walls.walls.grid[gridx][gridy].zone;
				Walls.walls.grid[gridx][gridy] = new Tile(gridx, gridy, WallType.WALL, "wood");
				Walls.walls.grid[gridx][gridy].zone = oldzone;
				
				--numWood;
			}
		}));
		
//		add(displayPosition, displaySize);
	}
}
