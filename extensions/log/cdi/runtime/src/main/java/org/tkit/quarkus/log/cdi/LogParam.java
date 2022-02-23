package org.tkit.quarkus.log.cdi;

import java.util.List;
import java.util.function.Function;

public interface LogParam {

    default List<Item> getAssignableFrom() {
        return List.of();
    }

    default List<Item> getClasses() {
        return List.of();
    }

    default Item item(int priority, Class<?> clazz, Function<Object, String> fn) {
        Item item = new Item();
        item.clazz = clazz;
        item.priority = priority;
        item.fn = fn;
        return item;
    }

    class Item {

        public Class<?> clazz;

        public Function<Object, String> fn;

        public int priority;

    }
}
