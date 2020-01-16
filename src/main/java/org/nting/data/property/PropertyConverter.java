package org.nting.data.property;

import java.util.Objects;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

import com.google.common.base.Converter;

public class PropertyConverter<F, T> extends AbstractProperty<T> {

    private final Property<F> sourceProperty;
    private final Converter<F, T> converter;

    private Registration registrationOnSourceProperty;

    public PropertyConverter(Property<F> sourceProperty, Converter<F, T> converter) {
        this.sourceProperty = sourceProperty;
        this.converter = converter;
    }

    @Override
    public T getValue() {
        return converter.convert(sourceProperty.getValue());
    }

    @Override
    public void setValue(T newValue) {
        sourceProperty.setValue(converter.reverse().convert(newValue));
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        if (valueChangeListeners.size() == 0) {
            registrationOnSourceProperty = sourceProperty.addValueChangeListener(event -> {
                T prevValue = converter.convert(event.getPrevValue());
                if (!Objects.equals(prevValue, getValue())) {
                    fireValueChange(prevValue);
                }
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
