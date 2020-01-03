package org.nting.data.property;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class ObjectProperty<T> extends AbstractProperty<T> {

    private T value;

    public ObjectProperty(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T newValue) {
        if (!Objects.equals(value, newValue)) {
            T prevValue = value;
            value = newValue;

            fireValueChange(prevValue);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("value", value).toString();
    }
}
