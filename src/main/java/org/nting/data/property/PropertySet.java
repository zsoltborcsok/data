package org.nting.data.property;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.bean.RuntimeBean;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PropertySet implements RuntimeBean {

    private final List<ValueChangeListener<Object>> valueChangeListeners = Lists.newLinkedList();
    private final BiMap<String, Property<?>> nameToPropertyMap = HashBiMap.create();
    private final Map<String, Registration> valueChangeRegistrations = Maps.newHashMap();

    public <T> Property<T> addProperty(String propertyName, Property<T> property) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName), "Property name can not be null or empty");
        Preconditions.checkArgument(!nameToPropertyMap.containsKey(propertyName), "Property name must be unique");

        nameToPropertyMap.put(propertyName, property);
        Registration registration = property.addValueChangeListener(
                event -> fireValueChange(event.getProperty(), event.getPrevValue(), propertyName));
        valueChangeRegistrations.put(propertyName, registration);

        fireValueChange(property, property.getValue(), null, propertyName);
        return property;
    }

    public <T> Property<T> addObjectProperty(String propertyName, T defaultValue) {
        return addProperty(propertyName, new ObjectProperty<>(defaultValue));
    }

    public final <T> ListProperty<T> addListProperty(String propertyName, Iterable<? extends T> elements) {
        return (ListProperty<T>) addProperty(propertyName, new ListProperty<T>(elements));
    }

    public final <T> SetProperty<T> addSetProperty(String propertyName, Iterable<? extends T> elements) {
        return (SetProperty<T>) addProperty(propertyName, new SetProperty<T>(elements));
    }

    public final <K, V> MapProperty<K, V> addMapProperty(String propertyName) {
        return (MapProperty<K, V>) addProperty(propertyName, new MapProperty<K, V>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Property<T> getProperty(String propertyName) {
        return (Property<T>) nameToPropertyMap.get(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return nameToPropertyMap.keySet();
    }

    public String getPropertyName(Property<?> property) {
        return nameToPropertyMap.inverse().get(property);
    }

    public boolean removeProperty(String propertyName) {
        Property<?> property = nameToPropertyMap.remove(propertyName);
        if (property != null) {
            valueChangeRegistrations.remove(propertyName).remove();
            // From the perspective of the PropertySet, the value of the property becomes null.
            fireValueChange(property, null, property.getValue(), propertyName);
            return true;
        }

        return false;
    }

    public Registration addValueChangeListener(ValueChangeListener<Object> listener) {
        Preconditions.checkArgument(!valueChangeListeners.contains(listener));

        valueChangeListeners.add(listener);

        return () -> valueChangeListeners.remove(listener);
    }

    @SuppressWarnings("rawtypes")
    private void fireValueChange(Property property, Object prevValue, String propertyName) {
        fireValueChange(property, property.getValue(), prevValue, propertyName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void fireValueChange(Property property, Object value, Object prevValue, String propertyName) {
        ValueChangeEvent<Object> valueChangeEvent = new ValueChangeEvent<Object>(property, value, prevValue,
                propertyName);
        valueChangeListeners.stream().sorted(Comparator.comparingInt(ValueChangeListener::priority))
                .forEach(listener -> listener.valueChange(valueChangeEvent));
    }
}
