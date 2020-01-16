package org.nting.data.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ListProperty<E> extends AbstractProperty<List<E>> implements List<E> {

    private final List<E> value;

    @SafeVarargs
    public ListProperty(E... elements) {
        value = Lists.newArrayList(elements);
    }

    public ListProperty(Iterable<? extends E> elements) {
        value = Lists.newArrayList(elements);
    }

    @Override
    public List<E> getValue() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public void setValue(List<E> newValue) {
        if (!Objects.equals(value, newValue)) {
            List<E> prevValue = ImmutableList.copyOf(value);
            value.clear();
            value.addAll(newValue);

            fireValueChange(prevValue);
        }
    }

    @Override
    public void add(int location, E element) {
        wrapValueChange(() -> value.add(location, element));
    }

    @Override
    public boolean add(E e) {
        return wrapValueChange(() -> value.add(e));
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return wrapValueChange(() -> value.addAll(c));
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return wrapValueChange(() -> value.addAll(index, c));
    }

    @Override
    public void clear() {
        wrapValueChange(value::clear);
    }

    @Override
    public E remove(int location) {
        return wrapValueChange(() -> value.remove(location));
    }

    @Override
    public boolean remove(Object o) {
        return wrapValueChange(() -> value.remove(o));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrapValueChange(() -> value.removeAll(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrapValueChange(() -> value.retainAll(c));
    }

    @Override
    public E set(int location, E element) {
        return wrapValueChange(() -> value.set(location, element));
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public E get(int index) {
        return value.get(index);
    }

    @Override
    public boolean contains(Object object) {
        return value.contains(object);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
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
    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return value.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return value.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return value.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return value.subList(fromIndex, toIndex);
    }

    private void wrapValueChange(Runnable valueChangeFunction) {
        List<E> prevValue = ImmutableList.copyOf(value);
        valueChangeFunction.run();
        if (!Objects.equals(prevValue, value)) {
            fireValueChange(prevValue);
        }
    }

    private <RESULT> RESULT wrapValueChange(Supplier<RESULT> valueChangeFunction) {
        List<E> prevValue = ImmutableList.copyOf(value);
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
