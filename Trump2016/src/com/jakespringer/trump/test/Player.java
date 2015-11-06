package com.jakespringer.trump.test;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.util.function.BooleanSupplier;
import org.lwjgl.input.Keyboard;

public class Player extends AbstractEntity {

    private final double SPEED_COEFFICIENT = 2000;

    private Signal<Vec2> velocity;
    private Signal<Vec2> position;

    @Override
    public void create() {
        //Position and velocity
        velocity = new Signal<>(new Vec2())
                .sendOn(Input.whileKeyDown(Keyboard.KEY_D), (dt, v) -> v.withX(200))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_A), (dt, v) -> v.withX(-200));
        //Movement.makeWASDVelocitySystem(SPEED_COEFFICIENT).combine(Movement.makeFrictionSystem());
        position = new Signal<>(new Vec2());
        //Movement.makePositionUpdateSystem(velocity);
        add(velocity, position);

        //Collisions
        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), new Vec2(16, 16));
        add(Walls.makeCollisionSystem(position, velocity, new Vec2(16, 16)));

        //Jumping
        add(Input.whenKey(Keyboard.KEY_SPACE, true).filter($ -> onGround.getAsBoolean()).forEach($ -> velocity.edit(v -> v.withY(1000))));

        //Gravity
        onUpdate(dt -> velocity.edit(v -> v.add(new Vec2(0, -1000 * dt))));

        //Graphics
        onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(16, 16), Color4.RED, 20));

        //Shooting
        add(Input.whileMouseDown(0).forEach($ -> Reagan.world().add(new Bullet())));

        //Moving view
        onUpdate(dt -> Window.viewPos = position.get().interpolate(Window.viewPos, dt * 4));

        //Destroying self
        add(Input.whenKey(Keyboard.KEY_Q, true).forEach($ -> destroy()));
    }

    private class Bullet extends AbstractEntity {

        @Override
        public void create() {
            //Position and velocity
            Signal<Vec2> v2 = new Signal<>(Input.getMouse().subtract(position.get()).withLength(1000));
            Signal<Vec2> p2 = Movement.makePositionUpdateSystem(v2);
            p2.set(position.get());
            add(v2, p2);

            //Graphics
            onUpdate(dt -> Graphics2D.fillEllipse(p2.get(), new Vec2(4, 4), Color4.BLUE, 10));

            //Destroying self after time
            add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
                if (t > 1) {
                    destroy();
                }
                return t + dt;
            }));

            //Destroy on hit wall
            add(Reagan.continuous.filter(dt -> Walls.collisionAt(position.get(), new Vec2(4, 4))).forEach(dt -> destroy()));
        }
    }
}
