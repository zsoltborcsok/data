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
        return new ConditionTransform<>(getProperty(), Objects::isNull);
    }

    public Condition isNotNull() {
        return isNull().not();
    }

    public Condition is(T expectedValue) {
        return new ConditionTransform<>(getProperty(), value -> Objects.equals(value, expectedValue));
    }

    public Condition isNot(T value) {
        return is(value).not();
    }

    public Condition valueEquals(Property<T> otherProperty) {
        return new ConditionReducer<>(Lists.newArrayList(getProperty(), otherProperty),
                list -> Objects.equals(list.get(0), list.get(1)));
    }

    public Condition valueNotEquals(Property<T> model) {
        return valueEquals(model).not();
    }

    @SafeVarargs
    public final Condition isIn(T first, T... rest) {
        return isIn(Lists.asList(first, rest));
    }

    public Condition isIn(Collection<T> collection) {
        return new ConditionTransform<>(getProperty(),
                value -> collection.stream().anyMatch(item -> Objects.equals(item, value)));
    }

    @SafeVarargs
    public final Condition isNotIn(T first, T... others) {
        return isIn(first, others).not();
    }

    public Condition isNotIn(Collection<T> collection) {
        return isIn(collection).not();
    }

    public Condition check(Function<T, Boolean> function) {
        return new Condition.ConditionTransform<>(property, function);
    }

    protected Property<T> getProperty() {
        return property;
    }
}
