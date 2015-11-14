package core;

import com.jakespringer.reagan.graphics.Graphics2D;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import core.AbstractEntity.LAE;
import org.lwjgl.input.Keyboard;

public class Test {

    public static void main(String[] args) {
        Core.init();

        AbstractEntity ae = new LAE(player -> {
            Signal<Vec2> position = new Signal(new Vec2());
            player.add(position);

            player.add(Input.whileKeyDown(Keyboard.KEY_LEFT).forEach(dt -> position.edit(new Vec2(-500 * dt, 0)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_RIGHT).forEach(dt -> position.edit(new Vec2(500 * dt, 0)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_UP).forEach(dt -> position.edit(new Vec2(0, 500 * dt)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_DOWN).forEach(dt -> position.edit(new Vec2(0, -500 * dt)::add)));

            player.onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(20, 20), Color4.RED, 20));
        });
        ae.create();

        System.out.println(Destructible.all);
        //Core.interval(.5).onEvent(() -> System.out.println(Destructible.all));
        Core.delay(.5, () -> System.out.println(Destructible.all));

        Core.run();
    }
}
