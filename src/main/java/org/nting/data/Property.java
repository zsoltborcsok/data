package org.nting.data;

import java.util.Objects;
import java.util.function.Function;

public interface Property<T> {

    void setValue(T value);

    T getValue();

    Registration addValueChangeListener(ValueChangeListener<T> listener);

    default boolean hasValue() {
        return getValue() != null;
    }

    default boolean valueEquals(Object otherValue) {
        return Objects.equals(getValue(), otherValue);
    }

    default boolean valueEquals(Property<?> otherProperty) {
        return otherProperty != null && Objects.equals(getValue(), otherProperty.getValue());
    }

    default boolean valueEqualsAny(Object... otherValues) {
        T value = getValue();
        for (Object anotherValue : otherValues) {
            if (Objects.equals(value, anotherValue)) {
                return true;
            }
        }
        return false;
    }

    default void adjustValue(Function<T, T> valueConverter) {
        setValue(valueConverter.apply(getValue()));
    }
}
