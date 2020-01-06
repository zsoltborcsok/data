package org.nting.data.condition;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import org.nting.data.Property;
import org.nting.data.condition.Condition.ConditionReducer;
import org.nting.data.condition.Condition.ConditionTransform;

import com.google.common.collect.Lists;

public class ValueConditionBuilder<T> {

    private Property<T> property;

    public ValueConditionBuilder(Property<T> property) {
        this.property = property;
    }

    public Condition isNull() {
        return new ConditionTransform<>(Objects::isNull, getProperty());
    }

    public Condition isNotNull() {
        return isNull().not();
    }

    public Condition is(T expectedValue) {
        return new ConditionTransform<>(value -> Objects.equals(value, expectedValue), getProperty());
    }

    public Condition isNot(T value) {
        return is(value).not();
    }

    public Condition valueEquals(Property<T> otherProperty) {
        return new ConditionReducer<>(list -> Objects.equals(list.get(0), list.get(1)),
                Lists.newArrayList(getProperty(), otherProperty));
    }

    public Condition valueNotEquals(Property<T> model) {
        return valueEquals(model).not();
    }

    @SafeVarargs
    public final Condition isIn(T first, T... rest) {
        return isIn(Lists.asList(first, rest));
    }

    public Condition isIn(Collection<T> collection) {
        return new ConditionTransform<>(value -> collection.stream().anyMatch(item -> Objects.equals(item, value)),
                getProperty());
    }

    @SafeVarargs
    public final Condition isNotIn(T first, T... others) {
        return isIn(first, others).not();
    }

    public Condition isNotIn(Collection<T> collection) {
        return isIn(collection).not();
    }

    public Condition check(Function<T, Boolean> function) {
        return new Condition.ConditionTransform<>(function, property);
    }

    protected Property<T> getProperty() {
        return property;
    }
}
