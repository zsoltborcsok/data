package org.nting.data.query.filter;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;
import org.nting.data.query.QueryFilter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class ContainedIn implements QueryFilter {

    public final String propertyName;
    public final List<Object> values;

    public ContainedIn(String propertyName, Object... values) {
        this.propertyName = propertyName;
        this.values = ImmutableList.copyOf(values);
    }

    @Override
    public <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor) {
        Function<T, Object> getter = beanDescriptor.getPropertyDescriptor(propertyName).getter;
        return bean -> values.contains(getter.apply(bean));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyName", propertyName).add("values", values).toString();
    }
}
