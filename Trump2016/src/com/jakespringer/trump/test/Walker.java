package com.jakespringer.trump.test;

import java.util.List;
import java.util.function.BooleanSupplier;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.platfinder.Instruction;
import com.jakespringer.trump.platfinder.NodeConnector;
import com.jakespringer.trump.platfinder.PlatfinderGraph;
import com.jakespringer.trump.platfinder.Precalculator;

public class Walker extends AbstractEntity {

    public boolean[][] world;
    public PlatfinderGraph graph;
    public List<NodeConnector> nextSteps;
    public List<Instruction> immediateSteps;
    public Instruction rightNow;

    private Signal<Vec2> velocity;
    private Signal<Vec2> position;

    public Walker(boolean[][] grid) {
        world = grid;
    }

    @Override
    public void create() {
        Precalculator precal = new Precalculator();
        precal.setBlockArray(world);
        precal.setMoveSpeed(10.0);
        precal.setJumpSpeed(10.0);
        precal.setGravitySpeed(1.0);
        precal.precalculate();
        
        graph = precal.graph;
        nextSteps = graph.getShortestPath(graph.getNodeList().get(0), graph.getNodeList().get(1));

        final int xpos = graph.getNodeList().get(0).x; 
        final int ypos = graph.getNodeList().get(0).y;
        velocity = new Signal<>(new Vec2());
        position = new Signal<>(Walls.gridToActual(xpos-1, ypos+1));

        final int x2pos = graph.getNodeList().get(4).x;
        final int y2pos = graph.getNodeList().get(4).y;
        
        add(velocity, position);

        // Collisions
        BooleanSupplier onGround = () -> Walls.collisionAt(position.get().add(new Vec2(0, -1)), new Vec2(16, 16));
        add(Walls.makeCollisionSystem(position, velocity, new Vec2(16, 16)));

        // Gravity
        onUpdate(dt -> velocity.edit(v -> v.add(new Vec2(0, -1000 * dt))));

        // Graphics
        onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(16, 16), Color4.RED, 20));
        
        onUpdate(dt -> Graphics2D.fillEllipse(Walls.gridToActual(xpos, ypos), new Vec2(8, 8), Color4.GREEN, 16));
        onUpdate(dt -> Graphics2D.fillEllipse(Walls.gridToActual(x2pos, y2pos), new Vec2(8, 8), Color4.GREEN, 16));
        
        Window.viewPos = position.get();
        
        // Moving view
        onUpdate(dt -> Window.viewPos = position.get().interpolate(Window.viewPos, dt * 4));

        onUpdate(dt -> {
            if (nextSteps.isEmpty() && (immediateSteps == null || immediateSteps.isEmpty()) && rightNow == null)
                return;
            if (immediateSteps == null || immediateSteps.isEmpty())
                immediateSteps = nextSteps.remove(0).instructions;
            if (rightNow == null)
                rightNow = immediateSteps.remove(0);
            
            if (rightNow.delay > 0) {
                rightNow.delay -= dt;
                return;
            }

            if (rightNow.amount > 0) {
                if (rightNow.type == Instruction.Type.MOVE_LEFT) {
                    rightNow.amount -= Math.abs(velocity.get().x * dt);
                    velocity.set(velocity.get().withX(-200));
                    
                    System.out.println("amountl " + rightNow.amount);
                    return;
                }
                if (rightNow.type == Instruction.Type.MOVE_RIGHT) {
                    rightNow.amount -= Math.abs(velocity.get().x * dt);
                    velocity.set(velocity.get().withX(200));
                    
                    System.out.println("amountr " + rightNow.amount);
                    return;
                }
                
                System.out.println("a " + rightNow.amount);
            } else {
                velocity.set(velocity.get().withX(0));
            }
            
            if (rightNow.type == Instruction.Type.FALL) {
//                rightNow.amount -= velocity.get().y * dt;
                velocity.set(velocity.get().withX(0));
            }
            if (rightNow.type == Instruction.Type.JUMP) {
//                rightNow.amount -= velocity.get().y * dt;
                velocity.set(velocity.get().withY(1000));
            }

            rightNow = null;
        });
    }
}
