package org.nting.data.query.filter;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;
import org.nting.data.query.QueryFilter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class ContainsAll implements QueryFilter {

    public final String propertyName;
    public final List<Object> values;

    public ContainsAll(String propertyName, Object... values) {
        this.propertyName = propertyName;
        this.values = ImmutableList.copyOf(values);
    }

    /** Works only for properties with Collection type. */
    @Override
    public <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor) {
        Function<T, Object> getter = beanDescriptor.getPropertyDescriptor(propertyName).getter;
        return bean -> ((Collection<?>) getter.apply(bean)).containsAll(values);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyName", propertyName).add("values", values).toString();
    }
}
