package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SetPropertyTest {

    private SetProperty<Integer> setProperty;

    @Mock
    private ValueChangeListener<Set<Integer>> valueChangeListener;

    @Before
    public void setUp() {
        setProperty = new SetProperty<>(10, 5, 20);
        setProperty.addValueChangeListener(valueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals(ImmutableSet.of(10, 5, 20), setProperty.getValue());
    }

    @Test
    public void testSetValue() {
        setProperty.setValue(ImmutableSet.of(19, 78));

        assertEquals(ImmutableSet.of(19, 78), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAdd() {
        setProperty.add(0);

        assertEquals(ImmutableSet.of(10, 5, 20, 0), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAddAll() {
        setProperty.addAll(ImmutableSet.of(0, 40));

        assertEquals(ImmutableSet.of(0, 5, 10, 20, 40), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testClear() {
        setProperty.clear();

        assertEquals(Collections.emptySet(), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemove() {
        setProperty.remove(5);

        assertEquals(ImmutableSet.of(10, 20), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemoveAll() {
        setProperty.removeAll(ImmutableSet.of(20, 5));

        assertEquals(ImmutableSet.of(10), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRetainAll() {
        setProperty.retainAll(ImmutableSet.of(5));

        assertEquals(ImmutableSet.of(5), setProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testSize() {
        assertEquals(3, setProperty.size());
    }

    @Test
    public void testContains() {
        assertTrue(setProperty.contains(10));
        assertFalse(setProperty.contains(0));
        assertFalse(setProperty.contains(99));
    }

    @Test
    public void testIsEmpty() {
        assertFalse(setProperty.isEmpty());

        setProperty.clear();

        assertTrue(setProperty.isEmpty());
    }

    @Test
    public void testContainsAll() {
        assertTrue(setProperty.containsAll(ImmutableSet.of(5, 20)));
        assertFalse(setProperty.containsAll(ImmutableSet.of(5, 21)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        setProperty.setValue(ImmutableSet.of(99));

        ArgumentCaptor<ValueChangeEvent<Set<Integer>>> valueChangeEvent = ArgumentCaptor
                .forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals(ImmutableSet.of(5, 10, 20), valueChangeEvent.getValue().getPrevValue());
        assertEquals(ImmutableSet.of(99), valueChangeEvent.getValue().getValue());
        assertSame(setProperty, valueChangeEvent.getValue().getProperty());
    }
}