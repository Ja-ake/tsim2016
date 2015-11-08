package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.Sprite;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.Mutable;
import com.jakespringer.trump.particle.ParticleBurst;
import com.jakespringer.trump.platfinder2.NodeGraph;
import com.jakespringer.trump.platfinder2.NodeGraph.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Robot extends AbstractEntity {

    public static final List<Robot> redList = new LinkedList();
    public static final List<Robot> blueList = new LinkedList();

    public static final Vec2 size = new Vec2(16, 16);

    public static Vec2 redGoal, blueGoal;

    public Signal<Vec2> velocity;
    public Signal<Vec2> position;
    public Signal<Double> health;
    public boolean team;

    @Override
    public void create() {
        //Position and velocity
        Signal<Double> speed = new Signal<>(150.);
        velocity = new Signal<>(new Vec2()).sendOn(Reagan.continuous, (dt, v) -> v.withX(0));
        //.forEach(v -> new RuntimeException(v.toString()).printStackTrace());
        position = new Signal<>(new Vec2());
        add(velocity, position);

        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), size, team);

        //Follow path
        Mutable<Connection> c = new Mutable(null);
        Mutable<Double> time = new Mutable(0.);
        onUpdate(dt -> {
            if (c.o != null) {
                Graphics2D.drawLine(c.o.from.p.toVec2(), c.o.to.p.toVec2(), new Color4(1, .5, 0), 2);
                if (time.o < c.o.instructions.jumpDelay) {
                    //Nothing
                } else if (time.o < c.o.instructions.time) {
                    velocity.edit(v -> v.withX(speed.get() * Math.signum(c.o.to.p.x - c.o.from.p.x)));
                } else {
                    c.o = null;
                }
            }
            time.o += dt;
            if (c.o == null) {
                Vec2 goal = team ? redGoal : blueGoal;
                if (goal != null) {
                    if (onGround.getAsBoolean()) {
                        List<Connection> list = (team ? NodeGraph.red : NodeGraph.blue).findPath(position.get(), goal, size);
                        if (list != null && !list.isEmpty()) {
                            c.o = list.get(0);
                            velocity.edit(v -> v.withY(c.o.instructions.jumpSpeed));
                            time.o = 0.;
                        }
                    }
                }
            }
        });

        //Collisions
        add(Walls.makeCollisionSystem(position, velocity, size, team));//.filter($ -> onGround.getAsBoolean())
        //.filter(i -> i % 2 == 1).forEach(i -> velocity.edit(v -> v.withY(600))));

        //Gravity
        onUpdate(dt -> velocity.edit(v -> v.add(new Vec2(0, -1500 * dt))));

        //Graphics
        Sprite sprite = new Sprite("man_walk", 8, 1);
        sprite.color = team ? new Color4(1, .5, .5) : new Color4(.5, .5, 1);
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

        //Health
        health = new Signal<>(100.);
        add(health, health.filter(d -> d < 0).forEach($ -> explode()));

        //Robot lists
        if (team) {
            redList.add(this);
        } else {
            blueList.add(this);
        }

        //Shooting
        add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
            Mutable<Double> fireTime = new Mutable(t);
            if (t > .5) {
                speed.set(150.);
                List<Robot> enemy = team ? blueList : redList;
                enemy.stream().sorted((r1, r2)
                        -> (int) Math.signum(r1.position.get().subtract(position.get()).lengthSquared() - r2.position.get().subtract(position.get()).lengthSquared()))
                        .findFirst().ifPresent(r -> {
                            if (r.position.get().subtract(position.get()).lengthSquared() < 100 * 100) {
                                Bullet b = new Bullet();
                                b.team = team;
                                Reagan.world().add(b);
                                b.position.set(position.get());
                                b.velocity.set(r.position.get().subtract(position.get()).withLength(600));

                                fireTime.o = .5 * Math.random();
                                speed.set(50.);
                            }
                        });
            }
            return dt + fireTime.o;
        }));

        //Capping zones
        onUpdate(dt -> {
            int zone = Walls.tileAt(position.get()).zone;
            Walls.walls.zoneControl[zone - 1] += ((team ? 1 : -1) - Walls.walls.zoneControl[zone - 1]) / 100;
            Walls.walls.zoneControl[zone - 1] = Math.min(1, Math.max(-1, Walls.walls.zoneControl[zone - 1]));
        });
    }

    @Override
    public void destroy() {
        (team ? redList : blueList).remove(this);
        super.destroy();
    }

    public void explode() {
        ParticleBurst p = new ParticleBurst();
        p.position = position.get();
        p.speed = 100;
        p.lifeTime = .5;
        p.number = 100;
        p.color = team ? Color4.RED : Color4.BLUE;
        Reagan.world().add(p);

        destroy();
    }
}
