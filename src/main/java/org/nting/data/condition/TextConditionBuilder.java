package org.nting.data.condition;

import org.nting.data.Property;
import org.nting.data.condition.Condition.ConditionTransform;

public class TextConditionBuilder extends ValueConditionBuilder<String> {

    public TextConditionBuilder(Property<String> property) {
        super(property);
    }

    public Condition matches(String regex) {
        return new ConditionTransform<>(getProperty(), value -> value != null && value.matches(regex));
    }

    public Condition isEmpty() {
        return new ConditionTransform<>(getProperty(), value -> value == null || value.length() < 1);
    }

    public Condition isNotEmpty() {
        return isEmpty().not();
    }

    public Condition isBlank() {
        return new ConditionTransform<>(getProperty(), value -> value == null || value.trim().length() < 1);
    }

    public Condition isNotBlank() {
        return isBlank().not();
    }

    public Condition equals(String text, boolean ignoreCase) {
        return new ConditionTransform<>(getProperty(), value -> {
            if (ignoreCase) {
                return value == null ? text == null : value.equalsIgnoreCase(text);
            } else {
                return value == null ? text == null : value.equals(text);
            }
        });
    }
}