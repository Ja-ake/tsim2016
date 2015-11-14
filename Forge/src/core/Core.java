package core;

import com.jakespringer.reagan.graphics.Window;
import com.jakespringer.reagan.graphics.loading.FontContainer;
import java.io.File;
import org.lwjgl.opengl.Display;

public abstract class Core {

    public static final Signal<Double> update = new Signal();
    public static int speed = 60;
    public static double timeMult = 1;

    public static void init() {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        Window.initialize(1200, 800, "So how are you today?");
        FontContainer.init();
    }

    public static void run() {
        long lastStep = System.nanoTime();
        while (!Display.isCloseRequested()) {
            //Timing
            long now = System.nanoTime();
            double deltaTime = (now - lastStep) * 0.000000001 * timeMult;
            lastStep = now;
            //Update
            update.set(deltaTime);
            //Graphics
            Display.update();
            Display.sync(speed);
        }
    }

    //Time utility functions
    public static void delay(double delay, Runnable r) {
        time().filter(t -> t > delay).first(1).onEvent(r);
    }

    public static EventStream interval(double interval) {
        Signal<Double> time = time();
        return time.filter(t -> t > interval).forEach(t -> time.set(t - interval));
    }

    public static Signal<Double> time() {
        return update.reduce(0., (dt, t) -> t + dt);
    }
}
