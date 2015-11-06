package com.jakespringer.reagne.test;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.game.AbstractEntity;
import com.jakespringer.reagne.gfx.Graphics2D;
import com.jakespringer.reagne.input.Input;
import com.jakespringer.reagne.math.Color4;
import com.jakespringer.reagne.math.Vec2;
import org.lwjgl.input.Keyboard;

public class Player extends AbstractEntity {

    private final double SPEED_COEFFICIENT = 2000;

    private Signal<Vec2> velocity;
    private Signal<Vec2> position;

    @Override
    public void create() {
        //Position and velocity
        velocity = Movement.makeWASDVelocitySystem(SPEED_COEFFICIENT).combine(Movement.makeFrictionSystem());
        position = Movement.makePositionUpdateSystem(velocity);
        add(velocity, position);

        //Graphics
        onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(16, 16), Color4.RED, 20));

        //Shooting
        add(Input.whileMouseDown(0).forEach($ -> Reagne.world().add(new AbstractEntity() {
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
                add(new Signal<>(0.).sendOn(Reagne.continuous, (dt, t) -> {
                    if (t > 1) {
                        destroy();
                    }
                    return t + dt;
                }));
            }
        })));

        //Destroying self
        add(Input.whenKey(Keyboard.KEY_Q, true).forEach($ -> destroy()));
    }
}
