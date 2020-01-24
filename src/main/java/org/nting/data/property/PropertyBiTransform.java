package org.nting.data.property;

import java.util.Objects;
import java.util.function.BiFunction;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

public class PropertyBiTransform<A, B, T> extends AbstractProperty<T> {

    private final Property<A> firstSourceProperty;
    private final Property<B> secondSourceProperty;
    private final BiFunction<A, B, T> biTransform;

    private Registration registrationOnFirstSourceProperty;
    private Registration registrationOnSecondSourceProperty;
    private T prevValue;

    public PropertyBiTransform(Property<A> firstSourceProperty, Property<B> secondSourceProperty,
            BiFunction<A, B, T> biTransform) {
        this.firstSourceProperty = firstSourceProperty;
        this.secondSourceProperty = secondSourceProperty;
        this.biTransform = biTransform;
    }

    @Override
    public T getValue() {
        return biTransform.apply(firstSourceProperty.getValue(), secondSourceProperty.getValue());
    }

    @Override
    public void setValue(T newValue) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        if (valueChangeListeners.size() == 0) {
            ValueChangeListener valueChangeHandler = event -> {
                T currentValue = getValue();
                if (!Objects.equals(prevValue, currentValue)) {
                    fireValueChange(prevValue);
                }
                prevValue = currentValue;
            };

            registrationOnFirstSourceProperty = firstSourceProperty.addValueChangeListener(valueChangeHandler);
            registrationOnSecondSourceProperty = secondSourceProperty.addValueChangeListener(valueChangeHandler);
            prevValue = getValue();
        }

        return super.addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener<T> listener) {
        super.removeValueChangeListener(listener);

        if (valueChangeListeners.size() == 0) {
            if (registrationOnFirstSourceProperty != null) {
                registrationOnFirstSourceProperty.remove();
                registrationOnFirstSourceProperty = null;
            }
            if (registrationOnSecondSourceProperty != null) {
                registrationOnSecondSourceProperty.remove();
                registrationOnSecondSourceProperty = null;
            }
        }
    }
}
