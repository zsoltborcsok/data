package org.nting.data.property;

import java.util.Collections;

import org.nting.data.Property;

public class PropertySetBuilder {

    private final PropertySet propertySet = new PropertySet();

    public PropertySetBuilder addCustomProperty(Object propertyId, Property<?> property) {
        propertySet.addCustomProperty(propertyId, property);
        return this;
    }

    public PropertySetBuilder addObjectProperty(Object propertyId, Object initialValue) {
        propertySet.addObjectProperty(propertyId, initialValue);
        return this;
    }

    public PropertySetBuilder addListProperty(Object propertyId) {
        propertySet.addListProperty(propertyId, Collections.emptyList());
        return this;
    }

    public PropertySetBuilder addSetProperty(Object propertyId) {
        propertySet.addSetProperty(propertyId, Collections.emptySet());
        return this;
    }

    public PropertySetBuilder addMapProperty(Object propertyId) {
        propertySet.addMapProperty(propertyId);
        return this;
    }

    public PropertySet build() {
        return propertySet;
    }
}
