package org.nting.data.query.filter;

import java.util.List;
import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;
import org.nting.data.query.QueryFilter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class And implements QueryFilter {

    public final List<QueryFilter> filters;

    public And(QueryFilter... filters) {
        this.filters = ImmutableList.copyOf(filters);
    }

    @Override
    public <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor) {
        return filters.stream().map(filter -> filter.toInMemoryFilter(beanDescriptor)).reduce(t -> true,
                Predicate::and);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("filters", filters).toString();
    }
}
