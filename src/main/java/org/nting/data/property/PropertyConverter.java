package org.nting.data.property;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

import com.google.common.base.Converter;

public class PropertyConverter<F, T> extends AbstractProperty<T> {

    private final Converter<F, T> converter;
    private final Property<F> sourceProperty;

    private Registration registrationOnSourceProperty;

    public PropertyConverter(Converter<F, T> converter, Property<F> sourceProperty) {
        this.converter = converter;
        this.sourceProperty = sourceProperty;
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
                fireValueChange(converter.convert(event.getPrevValue()));
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
