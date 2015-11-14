package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class EventStream extends Destructible {

    private final HashMap<Destructible, Runnable> toCall = new HashMap();

    private <R extends Destructible> R addChild(R child, Runnable r) {
        addChild(child);
        toCall.put(child, r);
        return child;
    }

    @Override
    protected void removeChild(Destructible d) {
        toCall.remove(d);
        super.removeChild(d);
    }

    public void sendEvent() {
        new ArrayList<>(toCall.values()).forEach(Runnable::run);
    }

    <R extends EventStream> R with(R r, Consumer<R> c) {
        return addChild(r, () -> c.accept(r));
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
        return addChild(toEventStream(), r);
    }

    public EventStream toEventStream() {
        return with(new EventStream(), EventStream::sendEvent);
    }
}
