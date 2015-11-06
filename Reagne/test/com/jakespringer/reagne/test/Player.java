package com.jakespringer.reagne.test;

import org.lwjgl.input.Keyboard;
import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.game.AbstractEntity;
import com.jakespringer.reagne.gfx.Graphics;
import com.jakespringer.reagne.input.Input;
import com.jakespringer.reagne.math.Color4;
import com.jakespringer.reagne.math.Vec2;

public class Player extends AbstractEntity {

    private final double SPEED_COEFFICIENT = 0.002;

    private Signal<Vec2> velocity;
    private Signal<Vec2> position;

    @Override
    public void create() {
        //Position and velocity
        velocity = Movement.makeWASDVelocitySystem(SPEED_COEFFICIENT).combine(Movement.makeFrictionSystem());
        position = Movement.makePositionUpdateSystem(velocity);
        add(velocity, position);

        //Graphics
        onUpdate(dt -> Graphics.drawCircle(position.get().x, position.get().y, 16.0, new Color4(1.0, 0.0, 0.0)));

        //Destroying self
        add(Input.whenKeyPressed(Keyboard.KEY_Q).forEach(x -> destroy()));
    }
}
