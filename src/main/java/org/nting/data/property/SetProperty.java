package org.nting.data.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SetProperty<E> extends AbstractProperty<Set<E>> implements Set<E> {

    private final Set<E> value;

    @SafeVarargs
    public SetProperty(E... elements) {
        value = Sets.newHashSet(elements);
    }

    public SetProperty(Iterable<? extends E> elements) {
        value = Sets.newHashSet(elements);
    }

    @Override
    public Set<E> getValue() {
        return Collections.unmodifiableSet(value);
    }

    @Override
    public void setValue(Set<E> newValue) {
        if (!Objects.equals(value, newValue)) {
            Set<E> prevValue = ImmutableSet.copyOf(value);
            value.clear();
            value.addAll(newValue);

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
    public boolean contains(Object o) {
        return value.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return value.iterator();
    }

    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return value.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return wrapValueChange(() -> value.add(e));
    }

    @Override
    public boolean remove(Object o) {
        return wrapValueChange(() -> value.remove(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return wrapValueChange(() -> value.addAll(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrapValueChange(() -> value.retainAll(c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrapValueChange(() -> value.removeAll(c));
    }

    @Override
    public void clear() {
        wrapValueChange(value::clear);
    }

    private void wrapValueChange(Runnable valueChangeFunction) {
        Set<E> prevValue = ImmutableSet.copyOf(value);
        valueChangeFunction.run();
        fireValueChange(prevValue);
    }

    private <RESULT> RESULT wrapValueChange(Supplier<RESULT> valueChangeFunction) {
        Set<E> prevValue = ImmutableSet.copyOf(value);
        RESULT result = valueChangeFunction.get();
        fireValueChange(prevValue);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("value", value).toString();
    }
}
