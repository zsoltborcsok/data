package org.nting.data.bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nting.data.Property;
import org.nting.data.property.ListProperty;
import org.nting.data.property.MapProperty;
import org.nting.data.property.SetProperty;

public interface RuntimeBean {

    Set<String> getPropertyNames();

    <T> Property<T> getProperty(String propertyName);

    default <T> Property<T> getProperty(Object propertyId) {
        return getProperty(propertyId.toString());
    }

    default <E> ListProperty<E> getListProperty(Object propertyId) {
        return (ListProperty<E>) this.<List<E>> getProperty(propertyId.toString());
    }

    default <E> SetProperty<E> getSetProperty(Object propertyId) {
        return (SetProperty<E>) this.<Set<E>> getProperty(propertyId.toString());
    }

    default <K, V> MapProperty<K, V> getMapProperty(Object propertyId) {
        return (MapProperty<K, V>) this.<Map<K, V>> getProperty(propertyId.toString());
    }

    default <T> T getValue(Object propertyId) {
        return this.<T> getProperty(propertyId).getValue();
    }

    default <T> void setValue(Object propertyId, T value) {
        getProperty(propertyId).setValue(value);
    }

    default BeanDescriptor<? extends RuntimeBean> beanDescriptor() {
        BeanDescriptor<? extends RuntimeBean> beanDescriptor = new BeanDescriptor<>(getClass());
        for (String propertyName : getPropertyNames()) {
            beanDescriptor.addPropertyDescriptor(propertyName, bean -> bean.getProperty(propertyName).getValue(),
                    (bean, value) -> bean.getProperty(propertyName).setValue(value));
        }

        return beanDescriptor;
    }
}
