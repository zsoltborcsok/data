package org.nting.data.condition;

import java.util.Collection;

import org.nting.data.Property;
import org.nting.data.condition.Condition.ConditionConverter;

import com.google.common.base.Converter;

public class Conditions {

    public static <T> ValueConditionBuilder<T> valueOf(Property<T> property) {
        return new ValueConditionBuilder<T>(property);
    }

    public static TextConditionBuilder textOf(Property<String> property) {
        return new TextConditionBuilder(property);
    }

    public static <E, T extends Collection<E>> CollectionConditionBuilder<E, T> itemsOf(Property<T> property) {
        return new CollectionConditionBuilder<>(property);
    }

    public static Condition convert(Property<Boolean> condition) {
        return new ConditionConverter<>(Converter.from(b -> b, b -> b), condition);
    }
}
