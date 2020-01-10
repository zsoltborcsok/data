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
    private final BiMap<String, Property<?>> idPropertyMap = HashBiMap.create();
    private final Map<String, Registration> valueChangeRegistrations = Maps.newHashMap();

    public <T> Property<T> addProperty(String id, Property<T> property) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Property id can not be null or empty");
        Preconditions.checkArgument(!idPropertyMap.containsKey(id), "Property id must be unique");

        idPropertyMap.put(id, property);
        Registration registration = property
                .addValueChangeListener(event -> fireValueChange(event.getProperty(), event.getPrevValue(), id));
        valueChangeRegistrations.put(id, registration);

        fireValueChange(property, property.getValue(), null, id);
        return property;
    }

    public <T> Property<T> addObjectProperty(String id, T defaultValue) {
        return addProperty(id, new ObjectProperty<>(defaultValue));
    }

    public final <T> ListProperty<T> addListProperty(String id, Iterable<? extends T> elements) {
        return (ListProperty<T>) addProperty(id, new ListProperty<T>(elements));
    }

    public final <T> SetProperty<T> addSetProperty(String id, Iterable<? extends T> elements) {
        return (SetProperty<T>) addProperty(id, new SetProperty<T>(elements));
    }

    public final <K, V> MapProperty<K, V> addMapProperty(String id) {
        return (MapProperty<K, V>) addProperty(id, new MapProperty<K, V>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Property<T> getProperty(String id) {
        return (Property<T>) idPropertyMap.get(id);
    }

    public Object getPropertyId(Property<?> property) {
        return idPropertyMap.inverse().get(property);
    }

    @Override
    public Set<String> getPropertyIds() {
        return idPropertyMap.keySet();
    }

    public boolean removeProperty(String id) {
        Property<?> property = idPropertyMap.remove(id);
        if (property != null) {
            valueChangeRegistrations.remove(id).remove();
            // From the perspective of the PropertySet, the value of the property becomes null.
            fireValueChange(property, null, property.getValue(), id);
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
    private void fireValueChange(Property property, Object prevValue, Object propertyId) {
        fireValueChange(property, property.getValue(), prevValue, propertyId);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void fireValueChange(Property property, Object value, Object prevValue, Object propertyId) {
        ValueChangeEvent<Object> valueChangeEvent = new ValueChangeEvent<Object>(property, value, prevValue,
                propertyId);
        valueChangeListeners.stream().sorted(Comparator.comparingInt(ValueChangeListener::priority))
                .forEach(listener -> listener.valueChange(valueChangeEvent));
    }
}
