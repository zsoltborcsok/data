package org.nting.data;

import java.util.EventObject;

import com.google.common.base.MoreObjects;

public class ValueChangeEvent<T> extends EventObject {

    private final T value;
    private final T prevValue;
    private final Object propertyId;

    public ValueChangeEvent(Property<T> property, T prevValue) {
        this(property, property.getValue(), prevValue, null);
    }

    public ValueChangeEvent(Property<T> property, T value, T prevValue, Object propertyId) {
        super(property);

        this.value = value;
        this.prevValue = prevValue;
        this.propertyId = propertyId;
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

    public Object getPropertyId() {
        return propertyId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("source", getSource()).add("value", value)
                .add("prevValue", prevValue).add("propertyId", propertyId).toString();
    }
}
