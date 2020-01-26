package org.nting.data.property;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class ObjectProperty<T> extends AbstractProperty<T> {

    private T value;
    private boolean readOnly = false;

    public ObjectProperty(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T newValue) {
        Preconditions.checkState(!readOnly, "Property is readonly!");

        if (!Objects.equals(value, newValue)) {
            T prevValue = value;
            value = newValue;

            fireValueChange(prevValue);
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("value", value).add("readOnly", readOnly).toString();
    }
}
