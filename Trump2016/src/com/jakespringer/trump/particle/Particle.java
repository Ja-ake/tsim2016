package com.jakespringer.trump.particle;

import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;

public class Particle {

    public Vec2 position;
    public Vec2 velocity;
    public Color4 color;
    public double lifeTime;

    public Particle(Vec2 position, Vec2 velocity, Color4 color, double lifeTime) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
        this.lifeTime = lifeTime;
    }
}
