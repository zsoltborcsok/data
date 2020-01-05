package org.nting.data.condition;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nting.data.Property;
import org.nting.data.property.PropertyConverter;
import org.nting.data.property.PropertyReducer;

import com.google.common.base.Converter;

public interface Condition extends Property<Boolean> {

    default Condition and(Property<Boolean> condition, Property<Boolean>... others) {
        return new ConditionReducer(conditions -> conditions.stream().reduce(Boolean::logicalAnd).get(),
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()));
    }

    default Condition or(Property<Boolean> condition, Property<Boolean>... others) {
        return new ConditionReducer(conditions -> conditions.stream().reduce(Boolean::logicalOr).get(),
                Stream.concat(Stream.of(this, condition), Stream.of(others)).collect(Collectors.toList()));
    }

    default Condition not() {
        return new ConditionConverter(Converter.from(b -> (b == null ? null : !b), b -> (b == null ? null : !b)), this);
    }

    class ConditionReducer extends PropertyReducer<Boolean, Boolean> implements Condition {

        public ConditionReducer(Function<List<Boolean>, Boolean> reducer, List<Property<Boolean>> dataSources) {
            super(reducer, dataSources);
        }
    }

    class ConditionConverter extends PropertyConverter<Boolean, Boolean> implements Condition {

        public ConditionConverter(Converter<Boolean, Boolean> converter, Property<Boolean> sourceProperty) {
            super(converter, sourceProperty);
        }
    }
}
