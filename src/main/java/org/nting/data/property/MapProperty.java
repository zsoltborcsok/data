package org.nting.data.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MapProperty<K, V> extends AbstractProperty<Map<K, V>> implements Map<K, V> {

    private final Map<K, V> value;

    public MapProperty() {
        value = Maps.newHashMap();
    }

    public MapProperty(Map<? extends K, ? extends V> map) {
        value = Maps.newHashMap(map);
    }

    @Override
    public Map<K, V> getValue() {
        return Collections.unmodifiableMap(value);
    }

    @Override
    public void setValue(Map<K, V> newValue) {
        if (!Objects.equals(value, newValue)) {
            Map<K, V> prevValue = ImmutableMap.copyOf(value);
            value.clear();
            value.putAll(newValue);

            fireValueChange(prevValue);
        }
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return value.get(key);
    }

    @Override
    public V put(K key, V value) {
        return wrapValueChange(() -> this.value.put(key, value));
    }

    @Override
    public V remove(Object key) {
        return wrapValueChange(() -> value.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        wrapValueChange(() -> value.putAll(m));
    }

    @Override
    public void clear() {
        wrapValueChange(value::clear);
    }

    @Override
    public Set<K> keySet() {
        return value.keySet();
    }

    @Override
    public Collection<V> values() {
        return value.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return value.entrySet();
    }

    private void wrapValueChange(Runnable valueChangeFunction) {
        Map<K, V> prevValue = ImmutableMap.copyOf(value);
        valueChangeFunction.run();
        if (!Objects.equals(prevValue, value)) {
            fireValueChange(prevValue);
        }
    }

    private <RESULT> RESULT wrapValueChange(Supplier<RESULT> valueChangeFunction) {
        Map<K, V> prevValue = ImmutableMap.copyOf(value);
        RESULT result = valueChangeFunction.get();
        if (!Objects.equals(prevValue, value)) {
            fireValueChange(prevValue);
        }
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("value", value).toString();
    }
}
