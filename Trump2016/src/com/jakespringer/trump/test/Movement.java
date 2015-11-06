package com.jakespringer.trump.test;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Vec2;
import org.lwjgl.input.Keyboard;

public class Movement {

    public static Signal<Vec2> makePositionUpdateSystem(final Signal<Vec2> velocity) {
        return new Signal<>(new Vec2())
                .sendOn(Reagan.continuous, (dt, x) -> x.add(velocity.get().multiply(dt)));
    }

    public static Signal<Vec2> makeWASDVelocitySystem(final double SPEED_COEFFICIENT) {
        return new Signal<>(new Vec2())
                .sendOn(Input.whileKeyDown(Keyboard.KEY_W), (dt, x) -> x.add(new Vec2(0.0, dt * SPEED_COEFFICIENT)))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_D), (dt, x) -> x.add(new Vec2(dt * SPEED_COEFFICIENT, 0.0)))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_A), (dt, x) -> x.add(new Vec2(-dt * SPEED_COEFFICIENT, 0.0)))
                .sendOn(Input.whileKeyDown(Keyboard.KEY_S), (dt, x) -> x.add(new Vec2(0.0, -dt * SPEED_COEFFICIENT)));
    }

    public static Signal<Vec2> makeFrictionSystem() {
        return new Signal<>(new Vec2()).sendOn(Reagan.continuous, (dt, x) -> x.multiply(0.9));
    }

    private Movement() {
    }
}
