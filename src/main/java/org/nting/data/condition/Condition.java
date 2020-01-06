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
        return new ConditionReducer<>(conditions -> conditions.stream().reduce(Boolean::logicalAnd).get(),
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()));
    }

    default Condition or(Property<Boolean> condition, Property<Boolean>... others) {
        return new ConditionReducer<>(conditions -> conditions.stream().reduce(Boolean::logicalOr).get(),
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()));
    }

    default Condition not() {
        return new ConditionConverter<>(Converter.from(b -> (b == null ? null : !b), b -> (b == null ? null : !b)),
                this);
    }

    class ConditionReducer<T> extends PropertyReducer<T, Boolean> implements Condition {

        public ConditionReducer(Function<List<T>, Boolean> reducer, List<Property<T>> dataSources) {
            super(reducer, dataSources);
        }
    }

    class ConditionConverter<T> extends PropertyConverter<T, Boolean> implements Condition {

        public ConditionConverter(Converter<T, Boolean> converter, Property<T> sourceProperty) {
            super(converter, sourceProperty);
        }
    }

    class ConditionTransform<T> extends PropertyTransform<T, Boolean> implements Condition {

        public ConditionTransform(Function<T, Boolean> transform, Property<T> sourceProperty) {
            super(transform, sourceProperty);
        }
    }
}
