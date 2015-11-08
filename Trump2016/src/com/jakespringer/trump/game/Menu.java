package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Texture;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.platfinder.NodeGraph;
import com.jakespringer.trump.ui.BuildMenu;
import com.jakespringer.trump.ui.ViewController;

public class Menu extends AbstractEntity {

    @Override
    public void create() {
        Texture tex = SpriteContainer.loadSprite("menu");
        Vec2 LL = new Vec2();
        Vec2 UR = new Vec2();
        onUpdate(dt -> {
            Graphics2D.drawSprite(tex, new Vec2(0, 0), new Vec2(1, 1), 0, Color4.WHITE);
            Graphics2D.drawRect(LL, UR.subtract(LL), Input.getMouseScreen().containedBy(LL, UR) ? new Color4(.8, .8, .8) : new Color4(.6, .6, .6));
            Graphics2D.drawText("Player", LL.interpolate(UR, .5));
        });
        add(Input.whenMouse(0, true).filter($ -> Input.getMouseScreen().containedBy(LL, UR)).forEach($ -> {
            //Start world - jake fill this out

            Reagan.world().addAndGet(new Walls()).loadImage();
            Reagan.world().addAndGet(new BuildMenu(true));
            Reagan.world().addAndGet(new ViewController()).position.set(new Vec2(1000, 1500));

            NodeGraph.red = new NodeGraph(Walls.walls.grid, true);
            Reagan.world().add(NodeGraph.red);
            NodeGraph.blue = new NodeGraph(Walls.walls.grid, false);

            destroy();
        }));
    }
}
