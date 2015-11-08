package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Sprite;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Robot extends AbstractEntity {

    private static final List<Robot> redList = new LinkedList();
    private static final List<Robot> blueList = new LinkedList();

    public Signal<Vec2> velocity;
    public Signal<Vec2> position;
    public Signal<Double> health;
    public boolean team;

    @Override
    public void create() {
        //Position and velocity
        velocity = new Signal<>(new Vec2()).sendOn(Reagan.continuous, (dt, v) -> v.withX(team ? 150 : -150));
        position = new Signal<>(new Vec2());
        add(velocity, position);

        //Collisions
        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), new Vec2(16, 16), team);
        add(Walls.makeCollisionSystem(position, velocity, new Vec2(16, 16), team).filter($ -> onGround.getAsBoolean())
                .filter(i -> i % 2 == 1).forEach(i -> velocity.edit(v -> v.withY(600))));

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

        //Shooting
        if (team) {
            redList.add(this);
        } else {
            blueList.add(this);
        }

    }

    public void explode() {

        destroy();
    }
}
