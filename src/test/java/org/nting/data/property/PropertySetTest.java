package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.nting.data.model.IssuePropertySet.Properties.STATUS;
import static org.nting.data.model.IssuePropertySet.Properties.TITLE;
import static org.nting.data.model.IssuePropertySet.Properties.VERSIONS;
import static org.nting.data.model.Status.IN_PROGRESS;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.Property;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.IssuePropertySet;
import org.nting.data.model.Priority;
import org.nting.data.model.Status;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class PropertySetTest {

    private IssuePropertySet issuePropertySet = new IssuePropertySet("tit.le", "descript.ion",
            ImmutableList.of("1.0.0", "1.1.0"), Status.NOT_STARTED, Priority.MINOR);

    @Mock
    private ValueChangeListener<String> valueChangeListener;

    @Test
    public void testStringProperty() {
        Property<String> objectProperty = issuePropertySet.getProperty(TITLE);

        objectProperty.setValue("H.E.L.P.");
        assertEquals("H.E.L.P.", objectProperty.getValue());
        assertEquals("H.E.L.P.", issuePropertySet.getValue(TITLE));

        objectProperty.setValue(null);
        assertNull(objectProperty.getValue());
        assertNull(issuePropertySet.getValue(TITLE));
    }

    @Test
    public void testEnumProperty() {
        Property<Status> objectProperty = issuePropertySet.getProperty(STATUS);

        objectProperty.setValue(IN_PROGRESS);
        assertEquals(IN_PROGRESS, objectProperty.getValue());
        assertEquals(IN_PROGRESS, issuePropertySet.getValue(STATUS));
    }

    @Test
    public void testListProperty() {
        ListProperty<String> listProperty = issuePropertySet.getListProperty(VERSIONS);

        listProperty.setValue(
                Stream.concat(listProperty.getValue().stream(), Stream.of("1.2.0")).collect(Collectors.toList()));
        assertTrue(listProperty.contains("1.2.0"));
        assertEquals(ImmutableList.of("1.0.0", "1.1.0", "1.2.0"), issuePropertySet.getValue(VERSIONS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        Property<String> objectProperty = issuePropertySet.getProperty(TITLE);
        objectProperty.addValueChangeListener(valueChangeListener);

        objectProperty.setValue("H.E.L.P.");

        ArgumentCaptor<ValueChangeEvent<String>> valueChangeEvent = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals("tit.le", valueChangeEvent.getValue().getPrevValue());
        assertEquals("H.E.L.P.", valueChangeEvent.getValue().getValue());
        assertSame(objectProperty, valueChangeEvent.getValue().getProperty());
    }
}