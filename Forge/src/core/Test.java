package core;

import com.jakespringer.reagan.graphics.Graphics2D;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.util.function.Consumer;
import org.lwjgl.input.Keyboard;

public class Test {

    public static void main(String[] args) {
        Core.init();

        AbstractEntity ae = AbstractEntity.from(player -> {
            Signal<Vec2> position = new Signal(new Vec2());

            player.add(Input.whileKeyDown(Keyboard.KEY_LEFT).forEach(dt -> position.edit(new Vec2(-500 * dt, 0)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_RIGHT).forEach(dt -> position.edit(new Vec2(500 * dt, 0)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_UP).forEach(dt -> position.edit(new Vec2(0, 500 * dt)::add)));
            player.add(Input.whileKeyDown(Keyboard.KEY_DOWN).forEach(dt -> position.edit(new Vec2(0, -500 * dt)::add)));

            player.onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(20, 20), Color4.RED, 20));
        });
        ae.create();

        Consumer<Integer> r;
        r = (x) -> System.out.println(x);
        r.accept(5);

        Core.run();

//        Signal<String> words = new Signal();
//        AbstractEntity ae = AbstractEntity.from(a -> {
//            a.add(words.forEach(System.out::println));
//        });
//        ae.create();
//        words.set("1", "2", "3");
//        ae.destroy();
//        words.set("4", "5", "6");
    }
}
