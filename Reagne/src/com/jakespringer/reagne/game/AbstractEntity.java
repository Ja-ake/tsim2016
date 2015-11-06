package com.jakespringer.reagne.game;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
        signals.add(Reagne.continuous.forEach(action));
    }
}
