package com.jakespringer.trump.game;

import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Camera;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;

public class Cloud extends AbstractEntity {

	Signal<Vec2> position;
	Signal<Vec2> velocity;
	
	public Vec2 size = new Vec2(0.2, 0.2);
	
	@Override
	public void create() {
		velocity = new Signal<Vec2>(new Vec2(10, 0));
		position = Movement.makePositionUpdateSystem(velocity);
		position.set(new Vec2(600, 600));
		
		onUpdate(dt -> {
            Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
			Graphics2D.drawSprite(SpriteContainer.loadSprite("cloud"), position.get(), size, 0.0, Color4.WHITE);
            Camera.setProjection2D(Window.LL(), Window.UR());
		});
		
		add(velocity, position);
	}
}
