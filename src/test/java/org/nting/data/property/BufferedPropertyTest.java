package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

@SuppressWarnings("unchecked")
public class BufferedPropertyTest {

    @Test
    public void testCommit() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);

        buffered.setValue("A");
        assertEquals("A", buffered.getValue());
        assertEquals("", source.getValue());

        buffered.commit();
        assertEquals("A", buffered.getValue());
        assertEquals("A", source.getValue());
    }

    @Test
    public void testDiscard() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);

        buffered.setValue("A");
        assertEquals("A", buffered.getValue());
        assertEquals("", source.getValue());

        buffered.discard();
        assertEquals("", buffered.getValue());
        assertEquals("", source.getValue());
    }

    @Test
    public void testNotBuffered() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);
        buffered.setBuffered(false);

        buffered.setValue("A");
        assertEquals("A", buffered.getValue());
        assertEquals("A", source.getValue());

        source.setValue("B");
        assertEquals("B", buffered.getValue());
        assertEquals("B", source.getValue());
    }

    @Test
    public void testValueChangeListen_NotBuffered() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);
        buffered.setBuffered(false);

        source.setValue("A");
        assertEquals("A", source.getValue());
        assertEquals("A", buffered.getValue());
    }

    @Test
    public void testValueChangeListen_Buffered() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);

        source.setValue("A");
        assertEquals("A", source.getValue());
        assertEquals("A", buffered.getValue());
    }

    @Test
    public void testValueChangeListen_Buffered_Modified() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);
        buffered.setValue("Z");

        source.setValue("A");
        assertEquals("A", source.getValue());
        assertEquals("Z", buffered.getValue());

        source.setValue("Z");
        source.setValue("C");
        assertEquals("C", source.getValue());
        assertEquals("Z", buffered.getValue());
    }

    @Test
    public void testSetBuffered() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);

        buffered.setValue("A");
        assertEquals("A", buffered.getValue());
        assertEquals("", source.getValue());

        buffered.setBuffered(false);
        assertFalse(buffered.isBuffered());
        assertEquals("A", buffered.getValue());
        assertEquals("A", source.getValue());
    }

    @Test
    public void testValueChangeNotify() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);
        ValueChangeListener<String> valueChangeListener = mock(ValueChangeListener.class);
        buffered.addValueChangeListener(valueChangeListener);

        buffered.setValue("A");
        buffered.setValue("B");
        buffered.setValue("C");
        buffered.setValue("C");
        verify(valueChangeListener, times(3)).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testValueChangeNotify_Source() {
        ObjectProperty<String> source = new ObjectProperty<>("");
        BufferedProperty<String> buffered = new BufferedProperty<>(source);
        ValueChangeListener<String> valueChangeListener = mock(ValueChangeListener.class);
        buffered.addValueChangeListener(valueChangeListener);

        source.setValue("A");
        source.setValue("B");
        source.setValue("C");
        source.setValue("C");
        buffered.setValue("Z");
        source.setValue("A");
        source.setValue("B");
        source.setValue("C");
        verify(valueChangeListener, times(4)).valueChange(any(ValueChangeEvent.class));
    }
}
