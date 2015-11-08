package com.jakespringer.trump.particle;

import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

public class ParticleBurst extends AbstractEntity {

    public Vec2 position;
    public double speed;
    public Color4 color;
    public double lifeTime;
    public int number;

    @Override
    public void create() {
        List<Particle> particles = new LinkedList();
        for (int i = 0; i < number; i++) {
            particles.add(new Particle(position, Vec2.random(speed), color, lifeTime));
        }
        onUpdate(dt -> {
            lifeTime -= dt;
            if (lifeTime < 0) {
                destroy();
            }
        });
        onUpdate(dt -> particles.forEach(p -> p.position = p.position.add(p.velocity.multiply(dt))));
        onUpdate(dt -> {
            glDisable(GL_TEXTURE_2D);
            //glEnable(GL_COLOR);
            GL11.glPointSize(2);
            glBegin(GL_POINTS);
            particles.forEach(p -> {
                p.color.glColor();
                p.position.glVertex();
            });
            glEnd();
        });
    }
}
