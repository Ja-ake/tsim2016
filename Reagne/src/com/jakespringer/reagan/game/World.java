package com.jakespringer.reagan.game;

import com.jakespringer.reagan.Reagan;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class World {

    private List<Entity> entityList;

    public World() {
        entityList = new LinkedList<>();
        Reagan.setWorld(this);
    }

    /**
     * Adds the supplied entity to the world and calls the entity's
     * {@link Entity#create()} method. Returns the id of the entity added.
     *
     * @param e the entity to add
     * @return the id of the entity added
     */
    public int add(final Entity e) {
        entityList.add(e);
        e.create();
        return entityList.size() - 1;
    }

    /**
     * Adds the supplied entity to the world and calls the entity's
     * {@link Entity#create()} method. Returns the entity added.
     *
     * @param <E> the type of the entity
     * @param e the entity to add
     * @return the entity added
     */
    public <E extends Entity> E addAndGet(final E e) {
        entityList.add(e);
        e.create();
        return e;
    }

    /**
     * Removes the entity from the world and calls the entity's
     * {@link Entity#destroy()} method. Returns the entity if it was removed,
     * otherwise returns null.
     *
     * @param e the entity to remove
     * @return the entity removed
     */
    public Entity remove(final Entity e) {
        if (entityList.remove(e)) {
            e.destroy();
            return e;
        }

        return null;
    }

    /**
     * Removes the entity from the world by id and calls the entity's
     * {@link Entity#destroy()} method. Returns the entity if it was removed,
     * otherwise returns null.
     *
     * @param id the id of the entity to remove
     * @return the entity removed
     */
    public Entity remove(final int id) {
        Entity e = entityList.remove(id);
        if (e == null) {
            return null;
        }
        e.destroy();
        return e;
    }
    
    public void removeIf(Predicate<Entity> pred) {
    	entityList.removeIf(e -> {
    		if (pred.test(e)) {
    			e.destroy();
    			return true;
    		}
    		
    		return false;
    	});
    }

    /**
     * Returns an java.util.stream.Stream of entities.
     *
     * @return the stream
     */
    public java.util.stream.Stream<Entity> stream() {
        return entityList.stream();
    }
}
