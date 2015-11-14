package core;

import java.util.Arrays;
import java.util.function.Consumer;

public abstract class AbstractEntity extends Destructible {

    protected void add(EventStream... e) {
        Arrays.asList(e).forEach(e2 -> e2.addChild(this));
    }

    protected abstract void create();

    public static AbstractEntity from(Consumer<AbstractEntity> create) {
        return new LAE(create);
    }

    protected void onUpdate(Consumer<Double> c) {
        add(Core.update.forEach(c));
    }

    public static class LAE extends AbstractEntity {

        private final Consumer<AbstractEntity> create;

        public LAE(Consumer<AbstractEntity> create) {
            this.create = create;
        }

        @Override
        protected void create() {
            create.accept(this);
        }
    }
}
