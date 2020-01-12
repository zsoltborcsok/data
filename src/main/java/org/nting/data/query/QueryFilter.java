package org.nting.data.query;

import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;

public interface QueryFilter {

    <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor);
}
