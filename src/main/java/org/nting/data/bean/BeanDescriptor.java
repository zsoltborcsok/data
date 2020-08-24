package org.nting.data.bean;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.nting.data.Property;
import org.nting.data.property.BeanProperty;

import com.google.common.base.MoreObjects;
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

    public RuntimeBean createRuntimeBean(BEAN bean) {
        return new RuntimeBeanImpl(bean);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("beanType", beanType)
                .add("propertyDescriptors", propertyDescriptors).toString();
    }

    public class RuntimeBeanImpl implements RuntimeBean {

        private final BEAN bean;
        private final Map<String, BeanProperty<BEAN, ?>> nameToPropertyMap = Maps.newHashMap();

        private RuntimeBeanImpl(BEAN bean) {
            this.bean = bean;
        }

        @Override
        public Set<String> getPropertyNames() {
            return BeanDescriptor.this.getPropertyNames();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Property<T> getProperty(String propertyName) {
            Preconditions.checkArgument(propertyDescriptors.containsKey(propertyName), "Missing property: '%s'",
                    propertyName);
            BeanProperty<BEAN, T> property = (BeanProperty<BEAN, T>) nameToPropertyMap.get(propertyName);
            if (property == null) {
                property = new BeanProperty<>(bean, BeanDescriptor.this.getPropertyDescriptor(propertyName));
                nameToPropertyMap.put(propertyName, property);
            }
            return property;
        }

        @Override
        public BeanDescriptor<BEAN> beanDescriptor() {
            return BeanDescriptor.this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("bean", bean).add("nameToPropertyMap", nameToPropertyMap)
                    .toString();
        }
    }
}
