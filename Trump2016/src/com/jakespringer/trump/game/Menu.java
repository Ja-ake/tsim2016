package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Texture;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.network.NetworkedMain;
import com.jakespringer.trump.platfinder.NodeGraph;
import com.jakespringer.trump.ui.BuildMenu;
import com.jakespringer.trump.ui.ViewController;

public class Menu extends AbstractEntity {
	public static BuildMenu buildMenu;
	
    @Override
    public void create() {
        Texture tex = SpriteContainer.loadSprite("menu");
        //FontContainer.add("Menu", "Helvetica", Font.BOLD, 70);
        Vec2 LL = new Vec2(355, 350);
        Vec2 UR = new Vec2(870, 545);
        onUpdate(dt -> {
            Graphics2D.drawSprite(tex, new Vec2(0, 0), new Vec2(1, 1), 0, Color4.WHITE);
            //System.out.println(Input.getMouseScreen().containedBy(LL, UR));
            //Graphics2D.fillEllipse(new Vec2(), UR, Color4.GREEN, 200);
            Graphics2D.fillRect(LL.subtract(new Vec2(600, 400)), UR.subtract(LL), Input.getMouseScreen().containedBy(LL, UR) ? new Color4(0, 0, 0, 0) : new Color4(.5, .5, .5, .4));
            //Graphics2D.drawText("Play", "Menu", new Vec2((LL.x + UR.x) / 2, UR.y).subtract(new Vec2(600 + FontContainer.get("Menu").getWidth("Play") / 2, 400)), Color.black);
        });
        add(Input.whenMouse(0, true).filter($ -> Input.getMouseScreen().containedBy(LL, UR)).forEach($ -> {

            for (int i=0; i<4; ++i) {
            	Cloud c = Reagan.world().addAndGet(new Cloud());
            	c.position.set(new Vec2(1000+-500*i*Math.random(), 700-Math.random()*140.));
            	double r = Math.random()*15;
            	c.velocity.set(new Vec2(r, 0));
            	c.size = new Vec2(r * (0.8/15.0), r * (0.8/15.0));
            }
            
            Reagan.periodic(10).forEach(() -> {
            	Cloud c = Reagan.world().addAndGet(new Cloud());
            	c.position.set(new Vec2(-500*Math.random(), 500+Math.random()*100.));
            	double r = Math.random()*15;
            	c.velocity.set(new Vec2(r, 0));
            	c.size = new Vec2(r * (0.8/15.0), r * (0.8/15.0));
            });
        	
            Reagan.world().addAndGet(new Walls()).loadImage();
            buildMenu = Reagan.world().addAndGet(new BuildMenu(true));
            Reagan.world().addAndGet(new ViewController()).position.set(new Vec2(1000, 1500));

            NodeGraph.red = new NodeGraph(Walls.walls.grid, true);
            Reagan.world().add(NodeGraph.red);
            NodeGraph.blue = new NodeGraph(Walls.walls.grid, false);
                        
//            NetworkedMain.run();
            destroy();
        }));
    }
}
