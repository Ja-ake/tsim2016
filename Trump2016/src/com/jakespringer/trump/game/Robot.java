package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Sprite;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.Mutable;
import static com.jakespringer.trump.game.Tile.WallType.*;
import com.jakespringer.trump.network.NetworkedMain;
import com.jakespringer.trump.network.RobotDestroyedEvent;
import com.jakespringer.trump.network.RobotStateEvent;
import com.jakespringer.trump.particle.ParticleBurst;
import com.jakespringer.trump.platfinder.NodeGraph;
import com.jakespringer.trump.platfinder.NodeGraph.Connection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Robot extends AbstractEntity {

    public static final List<Robot> redList = new LinkedList<>();
    public static final List<Robot> blueList = new LinkedList<>();

    public static final Vec2 size = new Vec2(14, 16);

    public static Vec2 redGoal, blueGoal;

    public Signal<Vec2> velocity;
    public Signal<Vec2> position;
    public Signal<Double> health;
    public boolean team;

    public int id;

    public Robot() {
        id = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public Robot(int i) {
        id = i;
    }

    @Override
    public void create() {
        //Position and velocity
        Signal<Double> speed = new Signal<>(145.);
        velocity = new Signal<>(new Vec2()).sendOn(Reagan.continuous, (dt, v) -> v.withX(0));
        position = new Signal<>(new Vec2());
        add(velocity, position);

        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), size, team);

        //Follow path
        Mutable<Connection> c = new Mutable<>(null);
        Mutable<Double> time = new Mutable<>(0.);
        onUpdate(dt -> {
            if (c.o != null) {
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
                        List<Connection> list = (team ? NodeGraph.red : NodeGraph.blue).findNearestPath(position.get(), goal, size);
                        if (list != null && !list.isEmpty()) {
                            c.o = list.get(0);
                            velocity.edit(v -> v.withY(c.o.instructions.jumpSpeed));
                            time.o = 0.;

                            if (time.o < c.o.instructions.jumpDelay) {
                                //Nothing
                            } else if (time.o < c.o.instructions.time) {
                                velocity.edit(v -> v.withX(speed.get() * Math.signum(c.o.to.p.x - c.o.from.p.x)));
                            } else {
                                c.o = null;
                            }
                        } else if (list != null && position.get().subtract(goal).length() > 100) {
                            explode(true);
                        }
                    }
                }
            }
        });

        //Push away from other Robots
        onUpdate(dt -> {
            redList.forEach(r -> {
                if (r != this) {
                    if (Walls.collideAABB(position.get(), size, r.position.get(), size)) {
                        if (position.get().x != r.position.get().x) {
                            velocity.edit(v -> v.add(position.get().subtract(r.position.get()).withY(0).withLength(10)));
                        }
                    }
                }
            });
            blueList.forEach(r -> {
                if (r != this) {
                    if (Walls.collideAABB(position.get(), size, r.position.get(), size)) {
                        if (position.get().x != r.position.get().x) {
                            velocity.edit(v -> v.add(position.get().subtract(r.position.get()).withY(0).withLength(10)));
                        }
                    }
                }
            });
        });

        //Collisions
        add(Walls.makeCollisionSystem(position, velocity, size, team));

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
        Mutable<Boolean> first = new Mutable<>(true);
        add(health, health.filter(d -> d < 0).filter(d -> first.o).forEach($ -> {
            first.o = false;
            explode(false);
        }));

        //Robot lists
        if (team) {
            redList.add(this);
        } else {
            blueList.add(this);
        }

        //Shooting
        add(new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
            Mutable<Double> fireTime = new Mutable<>(t);
            if (t > .5) {
                speed.set(150.);
                List<Robot> enemy = team ? blueList : redList;
                enemy.stream().filter(r -> r.position.get().subtract(position.get()).lengthSquared() < 100 * 100)
                        .sorted(Comparator.comparingDouble(r -> r.position.get().subtract(position.get()).lengthSquared()))
                        .findFirst().ifPresent(r -> {
                            Bullet b = new Bullet();
                            b.team = team;
                            Reagan.world().add(b);
                            b.position.set(position.get());
                            b.velocity.set(r.position.get().subtract(position.get()).withLength(600));

                            fireTime.o = .5 * Math.random();
                            speed.set(50.);
                        });
            }
            return dt + fireTime.o;
        }));

        //Capping zones
        onUpdate(dt -> {
            for (int x = 0; x < Walls.walls.width; x++) {
                for (int y = 0; y < Walls.walls.height; y++) {
                    Tile t = Walls.walls.grid[x][y];
                    if (t.center().subtract(position.get()).lengthSquared() < 400) {
                        t.control += ((team ? 1 : -1) - t.control) / 4;
                    } else {
                        t.control += ((team ? 1 : -1) - t.control) / t.center().subtract(position.get()).lengthSquared() * 100;
                    }
                }
            }
        });

        //Capping doors
        onUpdate(dt -> Walls.tilesAt(position.get(), size).stream().filter(t -> t.type == GRAY_DOOR).forEach(t
                -> t.change(team ? RED_DOOR : BLUE_DOOR, team ? "red_door" : "blue_door")));

        //Death to spikes
        onUpdate(dt -> {
            if (Walls.tileAt(position.get()).type == SPIKE) {
                explode(false);
            }
        });

        add(Reagan.periodic(2).forEach(() -> {
            if (NetworkedMain.networked && NetworkedMain.client.dictator) {
                NetworkedMain.networkHandler.submit(
                        new RobotStateEvent(id, position.get().x, position.get().y, velocity.get().x, velocity.get().y, team));
            }
        }));
    }

    @Override
    public void destroy() {
        if (NetworkedMain.networked) {
            NetworkedMain.networkHandler.submit(
                    new RobotDestroyedEvent(id));
        }

        Mutable<Signal> toDestroy = new Mutable(null);
        toDestroy.o = new Signal<>(0.).sendOn(Reagan.continuous, (dt, t) -> {
            if (t > 10) {
                toDestroy.o.remove();
                Statue.statues[team ? 0 : 1].spawn();
            }
            return t + dt;
        });

        (team ? redList : blueList).remove(this);
        super.destroy();
    }

    public void explode(boolean destroyBlocks) {
        ParticleBurst p = new ParticleBurst();
        p.position = position.get();
        p.speed = 300;
        p.lifeTime = .2;
        p.number = 100;
        p.color = team ? Color4.RED : Color4.BLUE;
        Reagan.world().add(p);

        if (destroyBlocks) {
            for (int x = 0; x < Walls.walls.width; x++) {
                for (int y = 0; y < Walls.walls.height; y++) {
                    Tile t = Walls.walls.grid[x][y];
                    if (t.type != AIR) {
                        if (position.get().subtract(t.center()).lengthSquared() < 80 * 80) {
                            if (t.type == BACKGROUND) {
                                t.change(AIR, null);
                            } else {
                                t.change(BACKGROUND, "stoneBackground");
                            }
                        }
                    }
                }
            }
        }

        (team ? blueList : redList).stream().forEach(r -> {
            if (position.get().subtract(r.position.get()).lengthSquared() < 80 * 80) {
                r.health.edit(d -> d - 50);
            }
        });

        destroy();
    }
}
