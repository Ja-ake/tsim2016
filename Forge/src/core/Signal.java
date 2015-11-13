package core;

import java.util.function.*;

public class Signal<T> extends EventStream implements Supplier<T> {

    private T value;

    public Signal() {
    }

    public Signal(T value) {
        this.value = value;
    }

    private Consumer<T> addListener(Consumer<T> c) {
        addListener(() -> c.accept(get()));
        return c;
    }

    public void edit(UnaryOperator<T> o) {
        set(o.apply(get()));
    }

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        sendEvent();
    }

    public void set(T... values) {
        for (T v : values) {
            set(v);
        }
    }

    //Interesting functions
    public <R> Signal<R> collect(R r, BiFunction<T, R, R> o) {
        return withRule(new Signal<>(r), r2 -> o.apply(get(), r2));
    }

    public Signal<T> combine(Signal<T>... other) {
        Signal<T> newSig = copy();
        for (Signal<T> o : other) {
            o.withRule(newSig, () -> o.get());
        }
        return newSig;
    }

    public Signal<T> copy() {
        return toSignalRule(() -> get());
    }

    public Signal<Integer> count() {
        return collect(0, (t, i) -> i + 1);
    }

    public <R, S> Signal<S> combineLatest(Signal<R> other, BiFunction<T, R, S> b) {
        return combineEventStreams(other).toSignalRule(() -> b.apply(get(), other.get()));
    }

    public <R> Signal<R> flatMap(Function<T, Signal<R>> f) {
        Signal<R> ret = f.apply(get());
        map(f).forEach(s -> s.forEach(r -> ret.set(r)));
        return ret;
    }

    public Signal<T> filter(Predicate<T> p) {
        return toSignal(s -> {
            if (p.test(get())) {
                s.set(get());
            }
        });
    }

    public Signal<T> filter(Supplier<Boolean> s) {
        return filter(t -> s.get());
    }

    public Signal<T> filterElse(Predicate<T> p, Consumer<Signal<T>> c) {
        return toSignal(s -> {
            if (p.test(get())) {
                s.set(get());
            } else {
                c.accept(this);
            }
        });
    }

    public Signal<T> first(int n) {
        return withRule(new Signal<>(n), i -> i - 1).filterElse(i -> i >= 0, EventStream::destroy).toSignalRule(() -> get());
    }

    public Signal<T> forEach(Consumer<T> c) {
        Signal newSig = copy();
        newSig.addListener(c);
        return newSig;
    }

    public <R> Signal<R> map(Function<T, R> f) {
        return toSignalRule(() -> f.apply(get()));
    }

    public <R> Signal<R> ofType(Class<R> c) {
        return toSignal(s -> {
            if (c.isInstance(get())) {
                s.set((R) get());
            }
        });
    }

    public Signal<T> reduce(BinaryOperator<T> o) {
        return collect(get(), o);
    }
}
