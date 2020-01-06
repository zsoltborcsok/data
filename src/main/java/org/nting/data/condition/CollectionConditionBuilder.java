package org.nting.data.condition;

import java.util.Collection;
import java.util.function.Function;

import org.nting.data.Property;

import com.google.common.collect.Lists;

public class CollectionConditionBuilder<E, T extends Collection<E>> {

    private Property<T> property;

    public CollectionConditionBuilder(Property<T> property) {
        this.property = property;
    }

    public Condition contains(E value) {
        return containsAny(value);
    }

    public Condition notContains(E value) {
        return contains(value).not();
    }

    @SafeVarargs
    public final Condition containsAny(E first, E... rest) {
        return containsAny(Lists.asList(first, rest));
    }

    public Condition containsAny(Collection<E> collection) {
        return new Condition.ConditionTransform<>(value -> collection.stream().anyMatch(value::contains), property);
    }

    @SafeVarargs
    public final Condition containsAll(E first, E... rest) {
        return containsAll(Lists.asList(first, rest));
    }

    public Condition containsAll(Collection<E> collection) {
        return new Condition.ConditionTransform<>(value -> value.containsAll(collection), property);
    }

    @SafeVarargs
    public final Condition containsNone(E first, E... others) {
        return containsAny(first, others).not();
    }

    public Condition isEmpty() {
        return new Condition.ConditionTransform<>(Collection::isEmpty, property);
    }

    public Condition isNotEmpty() {
        return isEmpty().not();
    }

    public Condition sizeIsEqualTo(int size) {
        return new Condition.ConditionTransform<>(value -> value.size() == size, property);
    }

    public Condition check(Function<T, Boolean> function) {
        return new Condition.ConditionTransform<>(function, property);
    }
}
