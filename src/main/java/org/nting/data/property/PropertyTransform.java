package org.nting.data.property;

import java.util.function.Function;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

public class PropertyTransform<F, T> extends AbstractProperty<T> {

    private final Property<F> sourceProperty;
    private final Function<F, T> transform;

    private Registration registrationOnSourceProperty;

    public PropertyTransform(Property<F> sourceProperty, Function<F, T> transform) {
        this.sourceProperty = sourceProperty;
        this.transform = transform;
    }

    @Override
    public T getValue() {
        return transform.apply(sourceProperty.getValue());
    }

    @Override
    public void setValue(T newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        if (valueChangeListeners.size() == 0) {
            registrationOnSourceProperty = sourceProperty.addValueChangeListener(event -> {
                fireValueChange(transform.apply(event.getPrevValue()));
            });
        }

        super.addValueChangeListener(listener);
        return () -> {
            valueChangeListeners.remove(listener);
            if (valueChangeListeners.size() == 0 && registrationOnSourceProperty != null) {
                registrationOnSourceProperty.remove();
                registrationOnSourceProperty = null;
            }
        };
    }
}
