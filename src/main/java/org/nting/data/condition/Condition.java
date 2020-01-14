package org.nting.data.condition;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nting.data.Property;
import org.nting.data.property.PropertyConverter;
import org.nting.data.property.PropertyReducer;
import org.nting.data.property.PropertyTransform;

import com.google.common.base.Converter;

public interface Condition extends Property<Boolean> {

    default Condition and(Property<Boolean> condition, Property<Boolean>... others) {
        return new ConditionReducer<>(
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()),
                conditions -> conditions.stream().reduce(Boolean::logicalAnd).get());
    }

    default Condition or(Property<Boolean> condition, Property<Boolean>... others) {
        return new ConditionReducer<>(
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()),
                conditions -> conditions.stream().reduce(Boolean::logicalOr).get());
    }

    default Condition not() {
        return new ConditionConverter<>(this,
                Converter.from(b -> (b == null ? null : !b), b -> (b == null ? null : !b)));
    }

    class ConditionReducer<T> extends PropertyReducer<T, Boolean> implements Condition {

        public ConditionReducer(List<Property<T>> sourceProperties, Function<List<T>, Boolean> reducer) {
            super(sourceProperties, reducer);
        }
    }

    class ConditionConverter<T> extends PropertyConverter<T, Boolean> implements Condition {

        public ConditionConverter(Property<T> sourceProperty, Converter<T, Boolean> converter) {
            super(sourceProperty, converter);
        }
    }

    class ConditionTransform<T> extends PropertyTransform<T, Boolean> implements Condition {

        public ConditionTransform(Property<T> sourceProperty, Function<T, Boolean> transform) {
            super(sourceProperty, transform);
        }
    }
}
