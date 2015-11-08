package com.jakespringer.trump.platfinder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.ImmutableTuple2;
import com.jakespringer.trump.game.Walls;

public class PlatTest {
	public static class LineSegment {
		public Vec2 pos1;
		public Vec2 pos2;
		
		public LineSegment(Vec2 p1, Vec2 p2) {
			pos1 = p1;
			pos2 = p2;
		}
	}
	
	public static void main(String[] args) throws IOException {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        final World world = new World();
        Window.initialize(1200, 800, "Test");
        
        boolean map[][] = world.addAndGet(new Walls()).loadText();
        Window.viewPos = new Vec2(600, 400);
        
		Platfinder finder = new Platfinder(map, 2, 1, 1);

		final List<LineSegment> paths = new LinkedList<>();
		final List<PlatNode> nodes = new LinkedList<>();
		
		final List<Instruction> instList = finder.getShortestPath(2, 9, 30, 10);
		
		for (Instruction in : instList) {
			System.out.print(in + ", ");
		}
		System.out.println();
		
		for (int i=0; i<finder.nodeMap.length; ++i) {
			for (int j=0; j<finder.nodeMap[i].length; ++j) {
				if (finder.nodeMap[i][j] != null) {
					nodes.add(finder.nodeMap[i][j]);
					for (ImmutableTuple2<PlatNode, Instruction> inst : finder.nodeMap[i][j].connections) {
						if (inst.left.x == 2) System.out.println("("+inst.left.x + ", "+inst.left.y+") -> " + "("+i+", "+j+"): "+inst.right.blocksToMove + " " + inst.right.verticalSpeed);
//						for (double n=0; n<inst.right.blocksToMove-1; ++n) {
//							Vec2 p1 = new Vec2(inst.left.x+n, inst.left.y+(inst.right.verticalSpeed*n + 0.5*(n*n)));
//							Vec2 p2 = new Vec2(inst.left.x+n+1, inst.left.y+(inst.right.verticalSpeed*(n+1) + 0.5*((n+1)*(n+1))));
//							paths.add(new LineSegment(p1, p2));
//						}
						
						paths.add(new LineSegment(new Vec2(i*36., j*36.), new Vec2(inst.left.x*36., inst.left.y*36.)));
					}
				}
			}
		}
        
        Reagan.continuous.forEach(dt -> {
        	Graphics2D.fillEllipse(new Vec2(10, 10), new Vec2(10, 10), Color4.BLACK, 20);
    		for (PlatNode p : nodes) {
    			Graphics2D.drawRect(new Vec2(p.x*36, p.y*36), new Vec2(36, 36), Color4.RED);
    		}
        	for (LineSegment ls : paths) {
        		Graphics2D.drawLine(ls.pos1.add(new Vec2(18, 18)), ls.pos2.add(new Vec2(18, 18)));
        		Graphics2D.fillEllipse(ls.pos1.add(new Vec2(18, 18)), new Vec2(6, 6), Color4.RED, 10);
        		Graphics2D.fillRect(ls.pos2.add(new Vec2(15, 15)), new Vec2(5, 5), Color4.BLUE);
        	}
        });
        
        Reagan.continuous.forEach(dt -> {
        	
        });
        
        Reagan.run(world);
	}
}
