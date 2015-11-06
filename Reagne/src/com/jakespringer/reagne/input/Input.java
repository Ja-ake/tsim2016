package com.jakespringer.reagne.input;

import static com.jakespringer.reagne.Signal.DEFAULT_OBJECT;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.util.Event;

public class Input {

    public static final boolean KEY_PRESSED = true;
    public static final boolean KEY_RELEASED = false;

    public static final Signal<Integer> onKeyPress = new Signal<>(0);
    public static final Signal<Integer> onKeyRelease = new Signal<>(0);
    public static final Signal<Integer> onMousePress = new Signal<>(0);
    public static final Signal<Integer> onMouseRelease = new Signal<>(0);

    private static final Signal<Object> eventLoop = new Signal<>(Signal.DEFAULT_OBJECT)
            .sendOn(Reagne.continuous, (dt, x) -> {
                while (Keyboard.next()) {
                    if (Keyboard.getEventKeyState() == KEY_PRESSED) {
                        onKeyPress.send(Integer.valueOf(Keyboard.getEventKey()));
                    } else /* ..getEventKeyState() == KEY_RELEASED */ {
                        onKeyRelease.send(Integer.valueOf(Keyboard.getEventKey()));
                    }
                }

                while (Mouse.next()) {
                    if (Mouse.getEventButtonState() == KEY_PRESSED) {
                        onMousePress.send(Mouse.getEventButton());
                    } else /* ..getEventButtonState() == KEY_RELEASED */ {
                        onMouseRelease.send(Mouse.getEventButton());
                    }
                }

                return Signal.DEFAULT_OBJECT;
            }).asRoot();

    public static Signal<Boolean> isKeyPressed(final int key) {
        return new Signal<>(false)
                .sendOn(onKeyPress.filter(x -> x == key), (k, x) -> true)
                .sendOn(onKeyRelease.filter(x -> x == key), (k, x) -> false);
    }

    public static Event whenKeyPressed(final int key) {
        return new Event()
                .sendOn(onKeyPress.filter(x -> x == key));
    }

    public static Event whenKeyReleased(final int key) {
        return new Event()
                .sendOn(onKeyPress.filter(x -> x == key));
    }

    public static Event whenMousePressed(final int key) {
        return new Event()
                .sendOn(onMousePress.filter(x -> x == key));
    }

    public static Event whenMouseReleased(final int key) {
        return new Event()
                .sendOn(onMousePress.filter(x -> x == key));
    }

    public static Signal<Double> whileKeyDown(final int key) {
        return Reagne.continuous.filter(isKeyPressed(key));
    }

    public static Signal<Double> whileKeyUp(final int key) {
        return Reagne.continuous.filter(x -> !isKeyPressed(key).get());
    }

    private Input() {
    }
}
