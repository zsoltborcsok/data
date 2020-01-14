package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Priority.MAJOR;
import static org.nting.data.model.Priority.MINOR;
import static org.nting.data.model.Priority.TRIVIAL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.Priority;

@RunWith(MockitoJUnitRunner.class)
public class ObjectPropertyTest {

    private ObjectProperty<Priority> objectProperty;

    @Mock
    private ValueChangeListener<Priority> valueChangeListener;
    private Registration valueChangeRegistration;

    @Before
    public void setUp() {
        objectProperty = new ObjectProperty<>(BLOCKER);
        valueChangeRegistration = objectProperty.addValueChangeListener(valueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals(BLOCKER, objectProperty.getValue());
    }

    @Test
    public void testSetValue() {
        objectProperty.setValue(TRIVIAL);

        assertEquals(TRIVIAL, objectProperty.getValue());
        verify(valueChangeListener).valueChange(any(ValueChangeEvent.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        objectProperty.setValue(MAJOR);

        ArgumentCaptor<ValueChangeEvent<Priority>> valueChangeEvent = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals(BLOCKER, valueChangeEvent.getValue().getPrevValue());
        assertEquals(MAJOR, valueChangeEvent.getValue().getValue());
        assertSame(objectProperty, valueChangeEvent.getValue().getProperty());
        reset(valueChangeListener);

        objectProperty.setValue(MAJOR);
        verify(valueChangeListener, never()).valueChange(any(ValueChangeEvent.class));

        valueChangeRegistration.remove();
        objectProperty.setValue(MINOR);
        verify(valueChangeListener, never()).valueChange(any(ValueChangeEvent.class));
    }
}
