package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Priority.MINOR;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.Priority;

import com.google.common.base.Enums;

@RunWith(MockitoJUnitRunner.class)
public class PropertyConverterTest {

    private ObjectProperty<Priority> priority;
    private PropertyConverter<Priority, String> priorityAsString;

    @Mock
    private ValueChangeListener<Priority> priorityValueChangeListener;
    @Mock
    private ValueChangeListener<String> stringValueChangeListener;
    @Captor
    private ArgumentCaptor<ValueChangeEvent<Priority>> priorityValueChangeEventCaptor;
    @Captor
    private ArgumentCaptor<ValueChangeEvent<String>> stringValueChangeEventCaptor;

    private Registration registration;

    @Before
    public void setUp() {
        priority = new ObjectProperty<>(BLOCKER);
        priorityAsString = new PropertyConverter<>(priority, Enums.stringConverter(Priority.class).reverse());

        priority.addValueChangeListener(priorityValueChangeListener);
        registration = priorityAsString.addValueChangeListener(stringValueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals("BLOCKER", priorityAsString.getValue());

        priority.setValue(MINOR);
        assertEquals("MINOR", priorityAsString.getValue());
    }

    @Test
    public void testSetValue() {
        priorityAsString.setValue("MINOR");
        assertEquals(MINOR, priority.getValue());

        verify(stringValueChangeListener).valueChange(stringValueChangeEventCaptor.capture());
        ValueChangeEvent<String> stringValueChangeEvent = stringValueChangeEventCaptor.getValue();
        assertEquals("BLOCKER", stringValueChangeEvent.getPrevValue());
        assertEquals("MINOR", stringValueChangeEvent.getValue());
        assertSame(priorityAsString, stringValueChangeEvent.getProperty());

        verify(priorityValueChangeListener).valueChange(priorityValueChangeEventCaptor.capture());
        ValueChangeEvent<Priority> priorityValueChangeEvent = priorityValueChangeEventCaptor.getValue();
        assertEquals(BLOCKER, priorityValueChangeEvent.getPrevValue());
        assertEquals(MINOR, priorityValueChangeEvent.getValue());
        assertSame(priority, priorityValueChangeEvent.getProperty());
    }

    @Test
    public void testSetValue_OnSource() {
        priority.setValue(MINOR);
        assertEquals("MINOR", priorityAsString.getValue());

        verify(stringValueChangeListener).valueChange(stringValueChangeEventCaptor.capture());
        ValueChangeEvent<String> stringValueChangeEvent = stringValueChangeEventCaptor.getValue();
        assertEquals("BLOCKER", stringValueChangeEvent.getPrevValue());
        assertEquals("MINOR", stringValueChangeEvent.getValue());
        assertSame(priorityAsString, stringValueChangeEvent.getProperty());

        verify(priorityValueChangeListener).valueChange(priorityValueChangeEventCaptor.capture());
        ValueChangeEvent<Priority> priorityValueChangeEvent = priorityValueChangeEventCaptor.getValue();
        assertEquals(BLOCKER, priorityValueChangeEvent.getPrevValue());
        assertEquals(MINOR, priorityValueChangeEvent.getValue());
        assertSame(priority, priorityValueChangeEvent.getProperty());
    }

    @Test
    public void testValueChange_Unregister() {
        registration.remove();

        priority.setValue(MINOR);
        assertEquals(MINOR, priority.getValue());
        assertEquals("MINOR", priorityAsString.getValue());

        verify(priorityValueChangeListener).valueChange(any(ValueChangeEvent.class));
        verify(stringValueChangeListener, never()).valueChange(any(ValueChangeEvent.class));
    }
}