package org.nting.data;

import org.nting.data.bean.RuntimeBean;

public interface PropertyId<BEAN extends RuntimeBean> {

    default <T> Property<T> propertyOf(BEAN bean) {
        return bean.getProperty(this);
    }

    default <T> T getValueOf(BEAN bean) {
        return bean.getValue(this);
    }

    default <T> void setValueOf(BEAN bean, T value) {
        bean.setValue(this, value);
    }
}
