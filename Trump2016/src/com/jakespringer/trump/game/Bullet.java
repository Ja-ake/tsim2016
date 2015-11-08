package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.util.List;

public class Bullet extends AbstractEntity {

    public static final Vec2 size = new Vec2(4, 4);

    public Signal<Vec2> position;
    public Signal<Vec2> velocity;
    public boolean team;

    @Override
    public void create() {
        //Position and velocity
        velocity = new Signal<>(new Vec2());
        position = Movement.makePositionUpdateSystem(velocity);
        position.set(position.get());
        add(velocity, position);

        //Destroy on hit wall
        add(Reagan.continuous.filter(dt -> Walls.collisionAt(position.get(), size, team)).forEach(dt -> destroy()));

        //Graphics
        Color4 color = team ? new Color4(.5, 0, 0) : new Color4(0, 0, .5);
        onUpdate(dt -> Graphics2D.fillEllipse(position.get(), size, color, 10));

        //Destroying self after time
        add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
            if (t > .5) {
                destroy();
            }
            return t + dt;
        }));

        //Hitting robots
        List<Robot> enemy = team ? Robot.blueList : Robot.redList;
        onUpdate(dt -> enemy.stream().filter(r -> Walls.collideAABB(position.get(), size, r.position.get(), Robot.size)).findAny().ifPresent(r -> r.health.edit(d -> {
            destroy();
            return d - 30;
        })));
    }
}
