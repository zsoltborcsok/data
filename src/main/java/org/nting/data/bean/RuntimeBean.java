package org.nting.data.bean;

import java.util.Set;

import org.nting.data.Property;

public interface RuntimeBean {

    Set<String> getPropertyIds();

    <T> Property<T> getProperty(String id);

    default BeanDescriptor<?> beanDescriptor() {
        BeanDescriptor<?> beanDescriptor = new BeanDescriptor<>(getClass());
        for (String propertyId : getPropertyIds()) {
            beanDescriptor.addPropertyDescriptor(propertyId, propertySet -> getProperty(propertyId).getValue(),
                    (propertySet, value) -> getProperty(propertyId).setValue(value));
        }

        return beanDescriptor;
    }
}
