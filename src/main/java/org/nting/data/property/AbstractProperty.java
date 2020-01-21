package org.nting.data.property;

import java.util.Comparator;
import java.util.List;

import org.nting.data.Property;
import org.nting.data.Property.ValueChangeListenerSupport;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class AbstractProperty<T> implements Property<T>, ValueChangeListenerSupport<T> {

    protected final List<ValueChangeListener<T>> valueChangeListeners = Lists.newLinkedList();

    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        Preconditions.checkArgument(!valueChangeListeners.contains(listener));

        valueChangeListeners.add(listener);

        return () -> removeValueChangeListener(listener);
    }

    @Override
    public List<ValueChangeListener<T>> getValueChangeListeners() {
        return ImmutableList.copyOf(valueChangeListeners);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener<T> listener) {
        valueChangeListeners.remove(listener);
    }

    protected void fireValueChange(T prevValue) {
        ValueChangeEvent<T> valueChangeEvent = new ValueChangeEvent<>(this, prevValue);
        valueChangeListeners.stream().sorted(Comparator.comparingInt(ValueChangeListener::priority))
                .forEach(listener -> listener.valueChange(valueChangeEvent));
    }
}
