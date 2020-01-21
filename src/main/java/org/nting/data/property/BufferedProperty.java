package org.nting.data.property;

import java.util.Objects;

import org.nting.data.Buffered;
import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

import com.google.common.base.MoreObjects;

public class BufferedProperty<T> extends AbstractProperty<T> implements Buffered {

    private final Property<T> property;

    private T bufferedValue;
    private boolean buffered = true;
    private boolean modified = false;

    private Registration registrationOnProperty;

    public BufferedProperty(Property<T> source) {
        this.property = source;

        bufferedValue = source.getValue();
    }

    @Override
    public void commit() {
        if (buffered && modified) {
            property.setValue(bufferedValue);
            modified = false;
        }
    }

    @Override
    public void discard() {
        if (buffered) {
            setValue(property.getValue());
            modified = false;
        }
    }

    @Override
    public void setBuffered(boolean buffered) {
        if (this.buffered == buffered) {
            return;
        }
        if (!buffered) {
            commit();
        }
        this.buffered = buffered;
    }

    @Override
    public boolean isBuffered() {
        return buffered;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public T getValue() {
        return modified ? bufferedValue : property.getValue();
    }

    @Override
    public void setValue(T newValue) {
        if (buffered) {
            if (!Objects.equals(bufferedValue, newValue)) {
                modified = true;
                T prevValue = bufferedValue;
                bufferedValue = newValue;
                fireValueChange(prevValue);
            }
        } else {
            property.setValue(newValue);
        }
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        if (valueChangeListeners.size() == 0) {
            registrationOnProperty = property.addValueChangeListener(event -> {
                if (!modified) {
                    fireValueChange(event.getPrevValue());
                }
            });
        }

        return super.addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener<T> listener) {
        super.removeValueChangeListener(listener);

        if (valueChangeListeners.size() == 0 && registrationOnProperty != null) {
            registrationOnProperty.remove();
            registrationOnProperty = null;
        }
    }

    public Property<T> getSource() {
        return property;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("property", property).add("bufferedValue", bufferedValue)
                .add("buffered", buffered).add("modified", modified).toString();
    }
}
