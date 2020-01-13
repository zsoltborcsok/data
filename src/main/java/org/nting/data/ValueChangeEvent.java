package org.nting.data;

import java.util.EventObject;

import com.google.common.base.MoreObjects;

public class ValueChangeEvent<T> extends EventObject {

    private final T value;
    private final T prevValue;
    private final String propertyName;

    public ValueChangeEvent(Property<T> property, T prevValue) {
        this(property, property.getValue(), prevValue, null);
    }

    public ValueChangeEvent(Property<T> property, T value, T prevValue, String propertyName) {
        super(property);

        this.value = value;
        this.prevValue = prevValue;
        this.propertyName = propertyName;
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

    public Object getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("source", getSource()).add("value", value)
                .add("prevValue", prevValue).add("propertyName", propertyName).toString();
    }
}
