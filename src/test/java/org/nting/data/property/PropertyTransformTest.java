package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
public class PropertyTransformTest {

    private ObjectProperty<String> text;
    private Property<String> abbreviatedText;

    @Mock
    private ValueChangeListener<String> textValueChangeListener;
    @Mock
    private ValueChangeListener<String> abbreviatedTextValueChangeListener;
    @Captor
    private ArgumentCaptor<ValueChangeEvent<String>> textValueChangeEventCaptor;
    @Captor
    private ArgumentCaptor<ValueChangeEvent<String>> abbreviatedTextValueChangeEventCaptor;

    private Registration registration;

    @Before
    public void setUp() {
        text = new ObjectProperty<>("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        abbreviatedText = PropertyTransform.abbreviate(text, 34);

        text.addValueChangeListener(textValueChangeListener);
        registration = abbreviatedText.addValueChangeListener(abbreviatedTextValueChangeListener);
    }

    @Test
    public void testGetValue() {
        assertEquals("Lorem ipsum dolor sit amet, con...", abbreviatedText.getValue());

        text.setValue("0123456789012345678901234567890123456789");
        assertEquals("0123456789012345678901234567890...", abbreviatedText.getValue());

        text.setValue("Lorem ipsum dolor sit amet");
        assertEquals("Lorem ipsum dolor sit amet", abbreviatedText.getValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetValue() {
        abbreviatedText.setValue("Lorem ipsum dolor");
    }

    @Test
    public void testSetValue_OnSource() {
        text.setValue("But I must explain to you how all this mistaken idea of denouncing.");
        assertEquals("But I must explain to you how a...", abbreviatedText.getValue());

        verify(textValueChangeListener).valueChange(textValueChangeEventCaptor.capture());
        ValueChangeEvent<String> textValueChangeEvent = textValueChangeEventCaptor.getValue();
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit.", textValueChangeEvent.getPrevValue());
        assertEquals("But I must explain to you how all this mistaken idea of denouncing.",
                textValueChangeEvent.getValue());
        assertSame(text, textValueChangeEvent.getProperty());

        verify(abbreviatedTextValueChangeListener).valueChange(abbreviatedTextValueChangeEventCaptor.capture());
        ValueChangeEvent<String> abbreviatedTextValueChangeEvent = abbreviatedTextValueChangeEventCaptor.getValue();
        assertEquals("Lorem ipsum dolor sit amet, con...", abbreviatedTextValueChangeEvent.getPrevValue());
        assertEquals("But I must explain to you how a...", abbreviatedTextValueChangeEvent.getValue());
        assertSame(abbreviatedText, abbreviatedTextValueChangeEvent.getProperty());
    }

    @Test
    public void testValueChange_Unregister() {
        registration.remove();

        text.setValue("tEXT");
        assertEquals("tEXT", text.getValue());
        assertEquals("tEXT", abbreviatedText.getValue());

        verify(textValueChangeListener).valueChange(any(ValueChangeEvent.class));
        verify(abbreviatedTextValueChangeListener, never()).valueChange(any(ValueChangeEvent.class));
    }
}