package com.jakespringer.reagan.input;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.Event;

public class Input {

    private static final Map<Integer, Signal<Boolean>> mouseMap = new HashMap();
    private static final Map<Integer, Signal<Boolean>> keyMap = new HashMap();
    
    public static final Signal<Integer> mouseWheel = new Signal<>(0);
    public static final Signal<Vec2> mouseWorldPosition = new Signal<>(new Vec2());

    private static Vec2 mouse;
    private static Vec2 mouseDelta;
    private static Vec2 mouseScreen;

    static {
        Reagan.continuous.forEach(dt -> {
            while (Keyboard.next()) {
                int key = Keyboard.getEventKey();
                if (!keyMap.containsKey(key)) {
                    keyMap.put(key, new Signal<>(Keyboard.getEventKeyState()));
                } else {
                    keyMap.get(key).set(Keyboard.getEventKeyState());
                }
            }

            while (Mouse.next()) {
                int button = Mouse.getEventButton();
                if (!mouseMap.containsKey(button)) {
                    mouseMap.put(button, new Signal<>(Mouse.getEventButtonState()));
                } else {
                    mouseMap.get(button).set(Mouse.getEventButtonState());
                }
            }
            
            int dWheel = Mouse.getDWheel();
            if (dWheel != 0) {
            	mouseWheel.set(dWheel);
            }
            
            mouseWorldPosition.sendOn(Reagan.continuous, (x, y) -> Input.getMouse());

            //Calculate mouse position
            double w = Display.getWidth();
            double h = Display.getHeight();
            double ar = Window.aspectRatio();
            double vw, vh;

            if (w / h > ar) {
                vw = ar * h;
                vh = h;
            } else {
                vw = w;
                vh = w / ar;
            }
            double left = (w - vw) / 2;
            double bottom = (h - vh) / 2;

            mouseScreen = new Vec2((Mouse.getX() - left) / vw, (Mouse.getY() - bottom) / vh).multiply(new Vec2(1200, 800));
            mouse = new Vec2((Mouse.getX() - left) / vw, (Mouse.getY() - bottom) / vh).multiply(Window.viewSize)
            		.subtract(Window.viewSize.multiply(.5)).add(Window.viewPos);
            mouseDelta = new Vec2(Mouse.getDX() / vw, Mouse.getDY() / vh).multiply(Window.viewSize);
        });
    }

    public static Vec2 getMouse() {
        return mouse;
    }

    public static Vec2 getMouseDelta() {
        return mouseDelta;
    }

    public static Vec2 getMouseScreen() {
        return mouseScreen;
    }

    public static Signal<Boolean> keySignal(int key) {
        if (!keyMap.containsKey(key)) {
            keyMap.put(key, new Signal<>(false));
        }
        return keyMap.get(key);
    }

    public static Signal<Boolean> mouseSignal(int button) {
        if (!mouseMap.containsKey(button)) {
            mouseMap.put(button, new Signal<>(false));
        }
        return mouseMap.get(button);
    }

    public static Event whenKey(int key, boolean val) {
        return keySignal(key).filter(x -> x == val).asEvent();
    }

    public static Event whenMouse(int button, boolean val) {
        return mouseSignal(button).filter(x -> x == val).asEvent();
    }

    public static Signal<Double> whileKeyDown(int key) {
        return Reagan.continuous.filter(keySignal(key));
    }

    public static Signal<Double> whileMouseDown(int mouse) {
        return Reagan.continuous.filter(mouseSignal(mouse));
    }

}
