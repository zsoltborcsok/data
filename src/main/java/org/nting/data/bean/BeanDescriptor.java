package org.nting.data.bean;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class BeanDescriptor<BEAN> {

    public final Class<BEAN> beanType;
    private final Map<String, PropertyDescriptor<BEAN, ?>> propertyDescriptors = Maps.newHashMap();

    public BeanDescriptor(Class<BEAN> beanType) {
        this.beanType = beanType;
    }

    public <T> BeanDescriptor<BEAN> addPropertyDescriptor(String propertyName, Function<BEAN, T> getter,
            BiConsumer<BEAN, T> setter) {
        return addPropertyDescriptor(new PropertyDescriptor<>(propertyName, getter, setter));
    }

    public <T> BeanDescriptor<BEAN> addPropertyDescriptor(PropertyDescriptor<BEAN, T> propertyDescriptor) {
        Preconditions.checkArgument(!propertyDescriptors.containsKey(propertyDescriptor.propertyName),
                "Property name must be unique");

        propertyDescriptors.put(propertyDescriptor.propertyName, propertyDescriptor);
        return this;
    }

    public Set<String> getPropertyNames() {
        return propertyDescriptors.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> PropertyDescriptor<BEAN, T> getPropertyDescriptor(String propertyName) {
        return (PropertyDescriptor<BEAN, T>) propertyDescriptors.get(propertyName);
    }
}
