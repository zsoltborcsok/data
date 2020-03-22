package org.nting.data.converter;

import static org.junit.Assert.assertEquals;
import static org.nting.data.converter.StringConverters.toEnumConverter;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Priority.CRITICAL;
import static org.nting.data.model.Priority.MAJOR;
import static org.nting.data.model.Priority.MINOR;
import static org.nting.data.model.Priority.TRIVIAL;

import org.junit.Before;
import org.junit.Test;
import org.nting.data.Property;
import org.nting.data.model.Priority;
import org.nting.data.property.ListProperty;

import com.google.common.collect.ImmutableList;

public class StringToListConverterTest {

    private ListProperty<Priority> prioritiesProperty;
    private Property<String> stringProperty;

    @Before
    public void setUp() {
        prioritiesProperty = new ListProperty<>(MAJOR, BLOCKER, CRITICAL);
        stringProperty = prioritiesProperty
                .convert(new StringToListConverter<>(", ", toEnumConverter(Priority.class)).reverse());
    }

    @Test
    public void testGetValue() {
        assertEquals("MAJOR, BLOCKER, CRITICAL", stringProperty.getValue());

        prioritiesProperty.add(MINOR);
        assertEquals("MAJOR, BLOCKER, CRITICAL, MINOR", stringProperty.getValue());

        prioritiesProperty.remove(0);
        assertEquals("BLOCKER, CRITICAL, MINOR", stringProperty.getValue());
    }

    @Test
    public void testSetValue() {
        stringProperty.setValue("MINOR, TRIVIAL");
        assertEquals(ImmutableList.of(MINOR, TRIVIAL), prioritiesProperty.getValue());
    }
}