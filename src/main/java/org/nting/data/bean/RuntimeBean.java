package org.nting.data.bean;

import java.util.Set;

import org.nting.data.Property;

public interface RuntimeBean {

    Set<String> getPropertyNames();

    <T> Property<T> getProperty(String propertyName);

    default <T> Property<T> getProperty(Object propertyId) {
        return getProperty(propertyId.toString());
    }

    default <T> T getValue(Object propertyId) {
        return this.<T> getProperty(propertyId).getValue();
    }

    default <T> void setValue(Object propertyId, T value) {
        getProperty(propertyId).setValue(value);
    }

    default BeanDescriptor<?> beanDescriptor() {
        BeanDescriptor<?> beanDescriptor = new BeanDescriptor<>(getClass());
        for (String propertyName : getPropertyNames()) {
            beanDescriptor.addPropertyDescriptor(propertyName, propertySet -> getProperty(propertyName).getValue(),
                    (propertySet, value) -> getProperty(propertyName).setValue(value));
        }

        return beanDescriptor;
    }
}
