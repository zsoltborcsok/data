package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class MapPropertyTest {

    private MapProperty<Integer, String> mapProperty;

    @Mock
    private ValueChangeListener<Map<Integer, String>> valueChangeListener;

    @Before
    public void setUp() {
        mapProperty = new MapProperty<>(ImmutableMap.of(1, "one", 5, "five", 10, "ten"));
        mapProperty.addValueChangeListener(valueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals(ImmutableMap.of(5, "five", 10, "ten", 1, "one"), mapProperty.getValue());
    }

    @Test
    public void testSetValue() {
        mapProperty.setValue(ImmutableMap.of(7, "seven", 2, "two"));

        assertEquals(ImmutableMap.of(7, "seven", 2, "two"), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testPut() {
        mapProperty.put(0, "zero");

        assertEquals(ImmutableMap.of(1, "one", 5, "five", 10, "ten", 0, "zero"), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);

        String previous = mapProperty.put(1, "van");

        assertEquals("one", previous);
        assertEquals(ImmutableMap.of(1, "van", 5, "five", 10, "ten", 0, "zero"), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAddAll() {
        mapProperty.putAll(ImmutableMap.of(0, "zero", 40, "forty"));

        assertEquals(ImmutableMap.of(1, "one", 5, "five", 10, "ten", 0, "zero", 40, "forty"), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);
    }

    @Test
    public void testClear() {
        mapProperty.clear();

        assertEquals(Collections.emptyMap(), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemove() {
        mapProperty.remove(5);

        assertEquals(ImmutableMap.of(1, "one", 10, "ten"), mapProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
        reset(valueChangeListener);
    }

    @Test
    public void testSize() {
        assertEquals(3, mapProperty.size());
    }

    @Test
    public void testContainsKey() {
        assertTrue(mapProperty.containsKey(10));
        assertFalse(mapProperty.containsKey(0));
        assertFalse(mapProperty.containsKey(99));
    }

    @Test
    public void testContainsValue() {
        assertTrue(mapProperty.containsValue("five"));
        assertFalse(mapProperty.containsValue("1"));
        assertFalse(mapProperty.containsValue("zero"));
    }

    @Test
    public void testIsEmpty() {
        assertFalse(mapProperty.isEmpty());

        mapProperty.clear();

        assertTrue(mapProperty.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        mapProperty.setValue(ImmutableMap.of(7, "seven", 2, "two"));

        ArgumentCaptor<ValueChangeEvent<Map<Integer, String>>> valueChangeEvent = ArgumentCaptor
                .forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals(ImmutableMap.of(5, "five", 10, "ten", 1, "one"), valueChangeEvent.getValue().getPrevValue());
        assertEquals(ImmutableMap.of(7, "seven", 2, "two"), valueChangeEvent.getValue().getValue());
        assertSame(mapProperty, valueChangeEvent.getValue().getProperty());
    }
}