package core;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EventStream {

    static class Parent {

        private final EventStream parent;
        private final Runnable toRemove;

        public Parent(EventStream parent, Runnable toRemove) {
            this.parent = parent;
            this.toRemove = toRemove;
        }
    }

    final List<Parent> parents = new LinkedList();
    final List<Runnable> children = new LinkedList();

    public Runnable addListener(Runnable r) {
        children.add(r);
        return r;
    }

    public void destroy() {
        children.clear();
        parents.forEach(p -> {
            p.parent.children.remove(p.toRemove);
            if (p.parent.children.isEmpty()) {
                p.parent.destroy();
            }
        });
    }

    public void sendEvent() {
        children.forEach(Runnable::run);
    }

    //Management
    public EventStream toEventStream() {
        return with(new EventStream(), EventStream::sendEvent);
    }

    <R> Signal<R> toSignal(Runnable r) {
        return with(new Signal(), r);
    }

    <R> Signal<R> toSignal(Consumer<Signal<R>> c) {
        return with(new Signal(), c);
    }

    <R> Signal<R> toSignalRule(Supplier<R> r) {
        return withRule(new Signal(), r);
    }

    <R> Signal<R> toSignalRule(UnaryOperator<R> r) {
        return withRule(new Signal(), r);
    }

    <R extends EventStream> R with(R r, Runnable run) {
        r.parents.add(new Parent(this, addListener(run)));
        return r;
    }

    <R extends EventStream> R with(R r, Consumer<R> c) {
        return with(r, () -> c.accept(r));
    }

    <R> Signal<R> withRule(Signal<R> s, Supplier<R> r) {
        return with(s, () -> s.set(r.get()));
    }

    <R> Signal<R> withRule(Signal<R> s, UnaryOperator<R> r) {
        return withRule(s, () -> r.apply(s.get()));
    }

    //Interesting functions
    public EventStream combineEventStreams(EventStream... other) {
        EventStream newStr = toEventStream();
        for (EventStream o : other) {
            o.with(newStr, EventStream::sendEvent);
        }
        return newStr;
    }

    public EventStream onEvent(Runnable r) {
        EventStream newStr = toEventStream();
        newStr.addListener(r);
        return newStr;
    }
}
