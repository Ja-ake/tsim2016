package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Sprite;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Vec2;
import java.util.function.BooleanSupplier;
import org.lwjgl.input.Keyboard;

public class Player extends AbstractEntity {

    public Signal<Vec2> velocity;
    public Signal<Vec2> position;

    @Override
    public void create() {
        //Position and velocity
        velocity = new Signal<>(new Vec2())
                .sendOn(Reagan.continuous, (dt, v) -> v.withX(0))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_D), (dt, v) -> v.withX(200))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_A), (dt, v) -> v.withX(-200));
        position = new Signal<>(new Vec2());
        add(velocity, position);

        //Collisions
        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), new Vec2(16, 16), true);
        add(Walls.makeCollisionSystem(position, velocity, new Vec2(16, 16), true));

        //Jumping
        add(Input.whenKey(Keyboard.KEY_SPACE, true).filter($ -> onGround.getAsBoolean()).forEach($ -> velocity.edit(v -> v.withY(600))));

        //Gravity
        onUpdate(dt -> velocity.edit(v -> v.add(new Vec2(0, -1000 * dt))));

        //Graphics
        Sprite sprite = new Sprite("man_walk", 8, 1);
        sprite.imageSpeed = 10;
        onUpdate(dt -> {
            if (velocity.get().x != 0) {
                sprite.scale = new Vec2(Math.signum(velocity.get().x), 1);
            } else {
                sprite.imageIndex = 0;
            }
            sprite.draw(position.get(), 0);
            if (onGround.getAsBoolean()) {
                sprite.setSprite("man_walk", 8, 1);
                sprite.imageIndex += dt * sprite.imageSpeed;
            } else {
                sprite.setSprite("man_jump");
                sprite.imageIndex = 0;
            }
        });

        //Creating robots
        add(Input.whenKey(Keyboard.KEY_R, true).forEach($ -> {
            Robot r = Reagan.world().addAndGet(new Robot());
            r.position.set(position.get());
        }));

        //Moving view
        onUpdate(dt -> Window.viewPos = position.get().interpolate(Window.viewPos, dt * 4));

        //Cheat
        add(Input.whileMouseDown(2).forEach(dt -> {
            position.set(Input.getMouse());
            velocity.set(new Vec2());
        }));
    }
}
