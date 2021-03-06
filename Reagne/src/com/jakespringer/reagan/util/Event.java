package com.jakespringer.reagan.util;

import java.util.function.Consumer;
import com.jakespringer.reagan.Signal;

public class Event extends Signal<Object> {

    public Event() {
        super(Signal.DEFAULT_OBJECT);
    }

    public void send() {
        set(Signal.DEFAULT_OBJECT);
    }
    
    public Event forEach(final Command command) {
        final Event newEvent = cloneAndRegisterE();
        newEvent.listeners.add(x -> command.act());
        return newEvent;
    }
    
    public <S> Event sendOn(final Signal<S> stream) {
        final Event newStream = cloneAndRegisterE();
        final Consumer<S> consume = x -> newStream.send();
        stream.forEach(consume);
        newStream.removeMeFrom.add(new ImmutableTuple2<>(stream, consume));
        return newStream;
    }
    
    public Signal<Object> asSignal() {
        return this;
    }
    
    ///
    /// PRIVATE HELPER METHODS BELOW
    ///
    private Event cloneAndRegisterE() {
        final Event cloned = new Event();
        final Consumer<Object> consumer = x -> cloned.set(x);
        listeners.add(consumer);
        cloned.removeMeFrom.addAll(removeMeFrom);
        cloned.removeMeFrom.add(new ImmutableTuple2<>(this, consumer));
        cloned.parent = this;
        return cloned;
    }

    private Event cloneAndDontRegisterE() {
        // no parent, parent is null
        final Event cloned = new Event();
        return cloned;
    }
}
