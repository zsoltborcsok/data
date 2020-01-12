package org.nting.data.query.filter;

import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;
import org.nting.data.query.QueryFilter;

public class EmptyFilter implements QueryFilter {

    @Override
    public <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor) {
        return t -> true;
    }
}
