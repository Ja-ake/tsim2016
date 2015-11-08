package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.network.NetworkedMain;
import com.jakespringer.trump.network.RobotCreatedEvent;

public class Statue extends AbstractEntity {

    public static final Statue[] statues = new Statue[2];

    public boolean teamRed;
    public Vec2 position;
    public final Vec2 size = new Vec2(1, 1).multiply(Walls.walls.wallSize);

    @Override
    public void create() {
        if (teamRed = statues[0] == null) {
            statues[0] = this;
        } else {
            statues[1] = this;
        }

        //Graphics
        onUpdate(dt -> Graphics2D.fillRect(position, size, new Color4(1, 1, 0)));

        for (int i = 0; i < 5; i++) {
            spawn();
        }
        //Creating robots
//        add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
//            if (t > 1) {
//                t = 0.;
//                if (!NetworkedMain.networked || NetworkedMain.client.dictator) {
//                    if ((teamRed ? Robot.redList : Robot.blueList).size() < 5) {
//
//                    }
//                }
//            }
//            return t + dt;
//        }));
    }

    public void spawn() {
        Robot r = new Robot();
        r.team = teamRed;
        Reagan.world().add(r);
        r.position.set(position.add(size.multiply(.5)));
        if (NetworkedMain.networked) {
            NetworkedMain.networkHandler.submit(
                    new RobotCreatedEvent(r.id, r.team));
        }
    }
}
