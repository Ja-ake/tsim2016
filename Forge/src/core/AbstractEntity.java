package core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractEntity {

    private final List references = new LinkedList();

    public void add(Object... o) {
        references.addAll(Arrays.asList(o));
    }

    public abstract void create();

    public void destroy() {
        references.clear();
    }
}
