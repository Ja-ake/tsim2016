package core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractEntity {

    private final List<EventStream> eventStreams = new LinkedList();

    public void add(EventStream... e) {
        eventStreams.addAll(Arrays.asList(e));
    }

    protected abstract void create();

    public void destroy() {
        eventStreams.forEach(EventStream::destroy);
        eventStreams.clear();
    }

    public static AbstractEntity from(Consumer<AbstractEntity> create) {
        return new AbstractEntity() {
            @Override
            public void create() {
                create.accept(this);
            }
        };
    }
    
    protected void onUpdate(Consumer<Double> c) {
        add(Core.update.forEach(c));
    }
}
