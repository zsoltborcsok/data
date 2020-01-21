package org.nting.data.property;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeListener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PropertyReducer<F, T> extends AbstractProperty<T> {

    @SafeVarargs
    public static <V> Property<V> firstNonNull(V defaultValue, Property<V>... properties) {
        return new PropertyReducer<>(list -> list.stream().filter(Objects::nonNull).findFirst().orElse(defaultValue),
                properties);
    }

    private final List<Property<F>> sourceProperties;
    private final Function<List<F>, T> reducer;

    private final List<Registration> registrations = Lists.newLinkedList();
    private T prevValue;

    @SafeVarargs
    public PropertyReducer(Function<List<F>, T> reducer, Property<F>... sourceProperties) {
        this(ImmutableList.copyOf(sourceProperties), reducer);
    }

    public PropertyReducer(List<Property<F>> sourceProperties, Function<List<F>, T> reducer) {
        this.sourceProperties = ImmutableList.copyOf(sourceProperties);
        this.reducer = reducer;
    }

    @Override
    public T getValue() {
        return computeValue();
    }

    @Override
    public void setValue(T newValue) {
        throw new UnsupportedOperationException();
    }

    private T computeValue() {
        return reducer.apply(sourceProperties.stream().map(Property::getValue).collect(Collectors.toList()));
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<T> listener) {
        if (valueChangeListeners.size() == 0) {
            ValueChangeListener<F> valueChangeHandler = event -> {
                T currentValue = computeValue();
                if (!Objects.equals(prevValue, currentValue)) {
                    fireValueChange(prevValue);
                }
                prevValue = currentValue;
            };
            sourceProperties.forEach(
                    sourceProperty -> registrations.add(sourceProperty.addValueChangeListener(valueChangeHandler)));
            prevValue = computeValue();
        }

        return super.addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener<T> listener) {
        super.removeValueChangeListener(listener);

        if (valueChangeListeners.size() == 0) {
            registrations.forEach(Registration::remove);
            registrations.clear();
        }
    }
}
