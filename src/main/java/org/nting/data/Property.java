package org.nting.data;

import java.util.Objects;
import java.util.function.Function;

import org.nting.data.property.PropertyConverter;
import org.nting.data.property.PropertyTransform;

import com.google.common.base.Converter;

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

    default <C> Property<C> convert(Converter<T, C> converter) {
        return new PropertyConverter<>(this, converter);
    }

    default <C> Property<C> transform(Function<T, C> transform) {
        return new PropertyTransform<>(this, transform);
    }
}
