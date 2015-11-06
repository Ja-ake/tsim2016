package com.jakespringer.reagan.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;

public abstract class AbstractEntity implements Entity {

    private final List<Signal> signals = new LinkedList();

    public void add(Signal... signalArray) {
        signals.addAll(Arrays.asList(signalArray));
    }

    @Override
    public void destroy() {
        signals.forEach(Signal::remove);
    }

    public void onUpdate(Consumer<Double> action) {
        signals.add(Reagan.continuous.forEach(action));
    }
}
