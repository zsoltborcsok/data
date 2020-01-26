package org.nting.data.property;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.nting.data.bean.PropertyDescriptor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class BeanProperty<BEAN, T> extends AbstractProperty<T> {

    private final BEAN bean;
    private final Function<BEAN, T> getter;
    private final BiConsumer<BEAN, T> setter;

    public BeanProperty(BEAN bean, PropertyDescriptor<BEAN, T> propertyDescriptor) {
        this(bean, propertyDescriptor.getter, propertyDescriptor.setter);
    }

    public BeanProperty(BEAN bean, Function<BEAN, T> getter, BiConsumer<BEAN, T> setter) {
        this.bean = bean;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public T getValue() {
        return getter.apply(bean);
    }

    @Override
    public void setValue(T newValue) {
        Preconditions.checkState(setter != null, "Property is readonly!");

        T prevValue = getValue();
        if (!Objects.equals(prevValue, newValue)) {
            setter.accept(bean, newValue);

            fireValueChange(prevValue);
        }
    }

    @Override
    public boolean isReadOnly() {
        return setter == null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("bean", bean).add("getter", getter).add("setter", setter)
                .add("value", getValue()).toString();
    }
}
