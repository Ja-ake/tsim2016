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

	private final Vec2 buildPosition;
	private final Vec2 buildSize;
	private final Vec2 destroyPosition;
	private final Vec2 destroySize;
	private Color4 buildColor;
	private Color4 destroyColor;
	private Texture texture;
	private int numWood = 128;
	private int numDestroy = 128;
	private boolean canBuild = false;
	private boolean canDestroy = false;
	private boolean buildSelection = false;
	private boolean destroySelection = false;
	private boolean redSide;
	
//	private Signal<Vec2> displayPosition;
//	private Signal<Vec2> displaySize;
	
	public BuildMenu(boolean red) {
		buildPosition = new Vec2(33, 32);
		buildSize = new Vec2(4, 4);
		destroyPosition = new Vec2(33+64, 32);
		destroySize = new Vec2(4, 4);
		buildColor = Color4.BLUE;
		destroyColor = Color4.BLUE;
		redSide = red;
	}
	
	@Override
	public void create() {
		texture = SpriteContainer.loadSprite("wood");
//		FontContainer.add("default", "arial", Font.PLAIN, 32);
		
		add(Reagan.continuous.forEach(dt -> {
			Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
			Graphics2D.drawSprite(texture, buildPosition, buildSize, 0, Color4.WHITE);
			Graphics2D.drawRect(buildPosition.subtract(buildSize.multiply(8)), buildSize.multiply(16), buildColor);
			Graphics2D.drawText("" + numWood, "Default", buildPosition.subtract(buildSize.withX(0).multiply(4)), Color.white);
			Graphics2D.fillRect(destroyPosition.subtract(destroySize.multiply(8)), destroySize.multiply(16), Color4.WHITE);
			Graphics2D.drawRect(destroyPosition.subtract(destroySize.multiply(8)), destroySize.multiply(16), destroyColor);
			Graphics2D.drawText("" + numDestroy, "Default", destroyPosition.subtract(destroySize.withX(0).multiply(4)), Color.black);
			Camera.setProjection2D(Window.LL(), Window.UR());
			
			Vec2 mpf = Walls.walls.snapToGrid(Input.mouseWorldPosition.get());
			int gridx = (int) (mpf.x/Walls.walls.wallSize);
			int gridy = (int) (mpf.y/Walls.walls.wallSize);
						
			if (buildSelection == true) {
				try {
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
			} else if (destroySelection == true) {
				try {
					if ((!redSide)?(Walls.walls.zoneControl[Walls.walls.grid[gridx][gridy].zone-1] < -0.5):(Walls.walls.zoneControl[Walls.walls.grid[gridx][gridy].zone-1] > 0.5) 
							&& (Walls.walls.grid[gridx][gridy].type == Tile.WallType.WALL)) {
						Graphics2D.fillRect(mpf, new Vec2(36, 36), Color4.GREEN);
						canDestroy = true;
					} else {
						Graphics2D.fillRect(mpf, new Vec2(36, 36), Color4.RED);
						canDestroy = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}));
		
		add(Input.whenMouse(0, true).forEach(() -> {
			Vec2 pos = Input.getMouseScreen();

			int gridx = (int) Walls.walls.snapToGrid(Input.getMouse()).divide(Walls.walls.wallSize).x;
			int gridy = (int) Walls.walls.snapToGrid(Input.getMouse()).divide(Walls.walls.wallSize).y;
			
			if (pos.x > buildPosition.subtract(buildSize.multiply(8)).x
					&& pos.y > buildPosition.subtract(buildSize.multiply(8)).y
					&& pos.x < buildPosition.subtract(buildSize.multiply(8)).add(buildSize.multiply(16)).x
					&& pos.y < buildPosition.subtract(buildSize.multiply(8)).add(buildSize.multiply(16)).y) {
				buildColor = buildColor.equals(Color4.BLUE) ? Color4.RED : Color4.BLUE;
				buildSelection = !buildSelection;
				destroySelection = false;
				destroyColor = destroyColor.BLUE;
			} if (pos.x > destroyPosition.subtract(destroySize.multiply(8)).x
					&& pos.y > destroyPosition.subtract(destroySize.multiply(8)).y
					&& pos.x < destroyPosition.subtract(destroySize.multiply(8)).add(destroySize.multiply(16)).x
					&& pos.y < destroyPosition.subtract(destroySize.multiply(8)).add(destroySize.multiply(16)).y) {
				destroyColor = destroyColor.equals(Color4.BLUE) ? Color4.RED : Color4.BLUE;
				destroySelection = !destroySelection;
				buildSelection = false;
				buildColor = Color4.BLUE;
			} else if (canBuild == true && buildSelection == true && numWood > 0) {
				int oldzone = Walls.walls.grid[gridx][gridy].zone;
				Walls.walls.grid[gridx][gridy] = new Tile(gridx, gridy, WallType.WALL, "wood");
				Walls.walls.grid[gridx][gridy].zone = oldzone;
				
				--numWood;
			} else if (canDestroy == true && destroySelection == true && numDestroy > 0) {
				int oldzone = Walls.walls.grid[gridx][gridy].zone;
				Walls.walls.grid[gridx][gridy] = new Tile(gridx, gridy, WallType.AIR, null);
				Walls.walls.grid[gridx][gridy].zone = oldzone;
				
				--numDestroy;
			}
		}));
		
//		add(displayPosition, displaySize);
	}
}
