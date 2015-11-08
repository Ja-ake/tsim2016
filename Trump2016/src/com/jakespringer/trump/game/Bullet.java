package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;

public class Bullet extends AbstractEntity {

    public Signal<Vec2> position;
    public Signal<Vec2> velocity;
    public boolean team;

    @Override
    public void create() {
        //Position and velocity
        Signal<Vec2> v2 = new Signal<>(Input.getMouse().subtract(position.get()).withLength(600));
        Signal<Vec2> p2 = Movement.makePositionUpdateSystem(v2);
        p2.set(position.get());
        add(v2, p2);

        //Destroy on hit wall
        add(Reagan.continuous.filter(dt -> Walls.collisionAt(p2.get(), new Vec2(4, 4), team)).forEach(dt -> destroy()));

        //Graphics
        Color4 color = team ? new Color4(.5, 0, 0) : new Color4(0, 0, .5);
        onUpdate(dt -> Graphics2D.fillEllipse(p2.get(), new Vec2(4, 4), color, 10));

        //Destroying self after time
        add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
            if (t > .5) {
                destroy();
            }
            return t + dt;
        }));
    }
}
