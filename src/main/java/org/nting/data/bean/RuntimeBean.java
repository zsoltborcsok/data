package org.nting.data.bean;

import java.util.Set;

import org.nting.data.Property;

public interface RuntimeBean {

    Set<String> getPropertyNames();

    <T> Property<T> getProperty(String propertyName);

    default BeanDescriptor<?> beanDescriptor() {
        BeanDescriptor<?> beanDescriptor = new BeanDescriptor<>(getClass());
        for (String propertyName : getPropertyNames()) {
            beanDescriptor.addPropertyDescriptor(propertyName, propertySet -> getProperty(propertyName).getValue(),
                    (propertySet, value) -> getProperty(propertyName).setValue(value));
        }

        return beanDescriptor;
    }
}
