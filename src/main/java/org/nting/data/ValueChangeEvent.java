package org.nting.data;

import java.util.EventObject;

import com.google.common.base.MoreObjects;

public class ValueChangeEvent<T> extends EventObject {

    private final T value;
    private final T prevValue;

    public ValueChangeEvent(Property<T> property, T prevValue) {
        super(property);

        this.value = property.getValue();
        this.prevValue = prevValue;
    }

    @SuppressWarnings("unchecked")
    public Property<T> getProperty() {
        return (Property<T>) getSource();
    }

    public T getValue() {
        return value;
    }

    public T getPrevValue() {
        return prevValue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("source", getSource()).add("value", value)
                .add("prevValue", prevValue).toString();
    }
}
