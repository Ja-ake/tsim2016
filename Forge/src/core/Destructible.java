package core;

import java.util.LinkedList;
import java.util.List;

public abstract class Destructible {

    public static final List<Destructible> all = new LinkedList();
    private static long maxID = 0;

    private final long id = maxID++;
    private final List<Destructible> parents = new LinkedList();
    private final List<Destructible> children = new LinkedList();

    protected Destructible() {
        all.add(this);
    }

    protected <R extends Destructible> R addChild(R child) {
        children.add(child);
        child.addParent(this);
        return child;
    }

    void addParent(Destructible parent) {
        parents.add(parent);
    }

    public void destroy() {
        children.clear();
        parents.forEach(p -> p.removeChild(this));
        parents.clear();
        all.remove(this);
        System.out.println(this + " has been destroyed");
    }

    protected void removeChild(Destructible d) {
        children.remove(d);
        if (children.isEmpty()) {
            destroy();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().charAt(0) + ":" + id;
    }
}
