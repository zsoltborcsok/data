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

    private <T> Property<T> addProperty(String propertyName, Property<T> property) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName), "Property name can not be null or empty");
        Preconditions.checkArgument(!nameToPropertyMap.containsKey(propertyName), "Property name must be unique");

        nameToPropertyMap.put(propertyName, property);
        Registration registration = property.addValueChangeListener(
                event -> fireValueChange(event.getProperty(), event.getPrevValue(), propertyName));
        valueChangeRegistrations.put(propertyName, registration);

        fireValueChange(property, property.getValue(), null, propertyName);
        return property;
    }

    public <T> Property<T> addCustomProperty(Object propertyId, Property<T> property) {
        return addProperty(propertyId.toString(), property);
    }

    public <T> ObjectProperty<T> addObjectProperty(Object propertyId, T initialValue) {
        return (ObjectProperty<T>) addProperty(propertyId.toString(), new ObjectProperty<>(initialValue));
    }

    public <T> ListProperty<T> addListProperty(Object propertyId, Iterable<? extends T> initialElements) {
        return (ListProperty<T>) addProperty(propertyId.toString(), new ListProperty<T>(initialElements));
    }

    public <T> SetProperty<T> addSetProperty(Object propertyId, Iterable<? extends T> initialElements) {
        return (SetProperty<T>) addProperty(propertyId.toString(), new SetProperty<T>(initialElements));
    }

    public <K, V> MapProperty<K, V> addMapProperty(Object propertyId) {
        return (MapProperty<K, V>) addProperty(propertyId.toString(), new MapProperty<K, V>());
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

    public boolean removeProperty(Object propertyId) {
        return removeProperty(propertyId.toString());
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
