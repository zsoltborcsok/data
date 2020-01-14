package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.nting.data.model.IssuePropertySet.Properties.VERSIONS;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.IssuePropertySet;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ListPropertyTest {

    private ListProperty<String> listProperty;

    @Mock
    private ValueChangeListener<List<String>> valueChangeListener;

    @Before
    public void setUp() {
        listProperty = new IssuePropertySet().getListProperty(VERSIONS);
        listProperty.add("1.0.0");
        listProperty.add("1.1.0");
        listProperty.addValueChangeListener(valueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals(ImmutableList.of("1.0.0", "1.1.0"), listProperty.getValue());
    }

    @Test
    public void testSetValue() {
        listProperty.setValue(ImmutableList.of("1.0.x", "1.1.x", "1.2.x"));

        assertEquals(ImmutableList.of("1.0.x", "1.1.x", "1.2.x"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAdd() {
        listProperty.add("x.y.z");

        assertEquals(ImmutableList.of("1.0.0", "1.1.0", "x.y.z"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);

        listProperty.add(0, "a.b.c");

        assertEquals(ImmutableList.of("a.b.c", "1.0.0", "1.1.0", "x.y.z"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAddAll() {
        listProperty.addAll(ImmutableList.of("X", "Y", "Z"));

        assertEquals(ImmutableList.of("1.0.0", "1.1.0", "X", "Y", "Z"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);

        listProperty.addAll(1, ImmutableList.of("A", "B", "C"));

        assertEquals(ImmutableList.of("1.0.0", "A", "B", "C", "1.1.0", "X", "Y", "Z"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testClear() {
        listProperty.clear();

        assertEquals(Collections.emptyList(), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemove() {
        listProperty.remove(1);

        assertEquals(ImmutableList.of("1.0.0"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);

        listProperty.remove("1.0.0");

        assertEquals(Collections.emptyList(), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemoveAll() {
        listProperty.removeAll(ImmutableList.of("1.0.0", "1.1.0"));

        assertEquals(Collections.emptyList(), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRetainAll() {
        listProperty.retainAll(ImmutableList.of("1.1.0"));

        assertEquals(ImmutableList.of("1.1.0"), listProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testSet() {
        listProperty.set(0, "1.0.x");
        listProperty.set(1, "1.1.x");

        assertEquals(ImmutableList.of("1.0.x", "1.1.x"), listProperty.getValue());
        verify(valueChangeListener, times(2)).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testSize() {
        assertEquals(2, listProperty.size());
    }

    @Test
    public void testGet() {
        assertEquals("1.0.0", listProperty.get(0));
        assertEquals("1.1.0", listProperty.get(1));
    }

    @Test
    public void testContains() {
        assertTrue(listProperty.contains("1.0.0"));
        assertFalse(listProperty.contains("1.0.x"));
    }

    @Test
    public void testIsEmpty() {
        assertFalse(listProperty.isEmpty());

        listProperty.clear();

        assertTrue(listProperty.isEmpty());
    }

    @Test
    public void testContainsAll() {
        assertTrue(listProperty.containsAll(ImmutableList.of("1.0.0", "1.1.0")));
        assertFalse(listProperty.containsAll(ImmutableList.of("1.0.0", "1.1.x")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        listProperty.setValue(ImmutableList.of("2.0.0"));

        ArgumentCaptor<ValueChangeEvent<List<String>>> valueChangeEvent = ArgumentCaptor
                .forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals(ImmutableList.of("1.0.0", "1.1.0"), valueChangeEvent.getValue().getPrevValue());
        assertEquals(ImmutableList.of("2.0.0"), valueChangeEvent.getValue().getValue());
        assertSame(listProperty, valueChangeEvent.getValue().getProperty());
    }
}