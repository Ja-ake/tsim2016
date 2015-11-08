package com.jakespringer.trump.ui;

import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.trump.game.Movement;
import com.jakespringer.trump.game.Walls;

public class ViewController extends AbstractEntity {

	public Signal<Vec2> position;
	private Signal<Vec2> velocity;
	private Signal<Vec2> scrollVelocity; 
	
	@Override
	public void create() {
//        velocity = new Signal<>(new Vec2())
//                .sendOn(Reagan.continuous, (dt, v) -> v.withX(0))
//                .sendOn(Input.whileKeyDown(Keyboard.KEY_D), (dt, v) -> v.withX(200))
//                .sendOn(Input.whileKeyDown(Keyboard.KEY_A), (dt, v) -> v.withX(-200))
//                .sendOn(Input.whileKeyDown(Keyboard.KEY_S), (dt, v) -> v.withY(-200))
//                .sendOn(Input.whileKeyDown(Keyboard.KEY_W), (dt, v) -> v.withY(200));
        velocity = Movement.makeWASDVelocitySystem(5000)
        		  .combine(Movement.makeFrictionSystem());
        position = Movement.makePositionUpdateSystem(velocity);
        scrollVelocity = Movement.makeFrictionSystem()
        		.sendOn(Input.mouseWheel, (i, v) -> {
        			return v.subtract(new Vec2(0, 0.4*i));
        		});
        
        add(position.forEach(v -> {
        	Window.viewPos = v;
        }));
        
        add(position, velocity, scrollVelocity);
        
        add(Reagan.continuous.forEach(dt -> {
        	if (scrollVelocity.get().y > 0 || Window.viewSize.lengthSquared() > 800000) {
        		Window.viewSize = Window.viewSize.add(Window.viewSize.normalize().multiply(scrollVelocity.get().y));
        	}
        	
        	if (Window.viewSize.lengthSquared() < 800000) {
        		Window.viewSize = Window.viewSize.withLength(Math.sqrt(800000-1));
        		scrollVelocity.set(new Vec2());
        	}
        	
        	if (Window.viewSize.y >= Walls.walls.height*Walls.walls.wallSize) {
        		double aspect = (Window.viewSize.x/Window.viewSize.y);
        		Window.viewSize = Window.viewSize.withY(Walls.walls.height*Walls.walls.wallSize).withX(aspect*Walls.walls.height*Walls.walls.wallSize);
        		scrollVelocity.set(new Vec2());
        	}
        	
        	if (Window.viewPos.x-(Window.viewSize.x/2.) < 0) {
        		position.set(Window.viewPos.withX((Window.viewSize.x/2.)+1));
        		velocity.set(velocity.get().withX(0));
        	}
        	
        	if (Window.viewPos.x+(Window.viewSize.x/2.) > Walls.walls.width*Walls.walls.wallSize) {
        		position.set(Window.viewPos.withX(Walls.walls.width*Walls.walls.wallSize-(Window.viewSize.x/2.)+1));
        		velocity.set(velocity.get().withX(0));
        	}
        	
        	if (Window.viewPos.y-(Window.viewSize.y/2.) < 0) {
        		position.set(Window.viewPos.withY((Window.viewSize.y/2.)+1));
        		velocity.set(velocity.get().withY(0));
        	}
        	
        	if (Window.viewPos.y+(Window.viewSize.y/2.) > Walls.walls.height*Walls.walls.wallSize) {
        		position.set(Window.viewPos.withY(Walls.walls.height*Walls.walls.wallSize-(Window.viewSize.y/2.)+1));
        		velocity.set(velocity.get().withY(0));
        	}
        }));
	}
}
