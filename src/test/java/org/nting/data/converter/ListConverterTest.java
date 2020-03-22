package org.nting.data.converter;

import static org.junit.Assert.assertEquals;
import static org.nting.data.converter.StringConverters.enumConverter;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Priority.CRITICAL;
import static org.nting.data.model.Priority.MAJOR;
import static org.nting.data.model.Priority.MINOR;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nting.data.Property;
import org.nting.data.model.Priority;
import org.nting.data.property.ListProperty;

import com.google.common.collect.ImmutableList;

public class ListConverterTest {

    private ListProperty<Priority> prioritiesProperty;
    private Property<List<String>> stringsProperty;

    @Before
    public void setUp() {
        prioritiesProperty = new ListProperty<>(MAJOR, BLOCKER, CRITICAL);
        stringsProperty = prioritiesProperty.convert(new ListConverter<>(enumConverter(Priority.class)));
    }

    @Test
    public void testGetValue() {
        assertEquals(ImmutableList.of("MAJOR", "BLOCKER", "CRITICAL"), stringsProperty.getValue());

        prioritiesProperty.add(MINOR);
        assertEquals(ImmutableList.of("MAJOR", "BLOCKER", "CRITICAL", "MINOR"), stringsProperty.getValue());

        prioritiesProperty.remove(0);
        assertEquals(ImmutableList.of("BLOCKER", "CRITICAL", "MINOR"), stringsProperty.getValue());
    }

    @Test
    public void testSetValue() {
        stringsProperty.setValue(ImmutableList.of("MINOR"));
        assertEquals(ImmutableList.of(MINOR), prioritiesProperty.getValue());
    }
}