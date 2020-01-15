package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

@RunWith(MockitoJUnitRunner.class)
public class PropertyReducerTest {

    private ObjectProperty<Integer> selection0, selection1, selection2;
    private Property<Integer> firstNotNullSelection;

    @Mock
    private ValueChangeListener<Integer> valueChangeListener;
    @Captor
    private ArgumentCaptor<ValueChangeEvent<Integer>> valueChangeEventCaptor;

    private Registration registration;

    @Before
    public void setUp() {
        selection0 = new ObjectProperty<>(null);
        selection1 = new ObjectProperty<>(null);
        selection2 = new ObjectProperty<>(null);
        firstNotNullSelection = PropertyReducer.firstNonNull(-1, selection0, selection1, selection2);

        registration = firstNotNullSelection.addValueChangeListener(valueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals(Integer.valueOf(-1), firstNotNullSelection.getValue());

        selection1.setValue(25);
        assertEquals(Integer.valueOf(25), firstNotNullSelection.getValue());

        selection0.setValue(50);
        assertEquals(Integer.valueOf(50), firstNotNullSelection.getValue());

        selection1.setValue(26);
        assertEquals(Integer.valueOf(50), firstNotNullSelection.getValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetValue() {
        firstNotNullSelection.setValue(99);
    }

    @Test
    public void testSetValue_OnSource() {
        selection2.setValue(25);
        assertEquals(Integer.valueOf(25), firstNotNullSelection.getValue());

        verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
        ValueChangeEvent<Integer> valueChangeEvent = valueChangeEventCaptor.getValue();
        assertEquals(Integer.valueOf(-1), valueChangeEvent.getPrevValue());
        assertEquals(Integer.valueOf(25), valueChangeEvent.getValue());
        assertSame(firstNotNullSelection, valueChangeEvent.getProperty());
        reset(valueChangeListener);

        selection1.setValue(44);
        assertEquals(Integer.valueOf(44), firstNotNullSelection.getValue());

        verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
        valueChangeEvent = valueChangeEventCaptor.getValue();
        assertEquals(Integer.valueOf(25), valueChangeEvent.getPrevValue());
        assertEquals(Integer.valueOf(44), valueChangeEvent.getValue());
        assertSame(firstNotNullSelection, valueChangeEvent.getProperty());
    }

    @Test
    public void testValueChange_Unregister() {
        registration.remove();

        selection1.setValue(25);
        assertEquals(Integer.valueOf(25), firstNotNullSelection.getValue());

        verify(valueChangeListener, never()).valueChange(any(ValueChangeEvent.class));
    }
}