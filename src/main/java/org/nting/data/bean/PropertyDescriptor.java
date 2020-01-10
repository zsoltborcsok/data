package org.nting.data.bean;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.base.MoreObjects;

public class PropertyDescriptor<BEAN, T> {

    public final String propertyName;
    public final Function<BEAN, T> getter;
    public final BiConsumer<BEAN, T> setter;

    public PropertyDescriptor(String propertyName, Function<BEAN, T> getter, BiConsumer<BEAN, T> setter) {
        this.propertyName = propertyName;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyDescriptor<?, ?> that = (PropertyDescriptor<?, ?>) o;
        return Objects.equals(propertyName, that.propertyName) && Objects.equals(getter, that.getter)
                && Objects.equals(setter, that.setter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, getter, setter);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyName", propertyName).add("getter", getter)
                .add("setter", setter).toString();
    }
}
