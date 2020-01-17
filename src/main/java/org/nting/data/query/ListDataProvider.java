package org.nting.data.query;

import static org.nting.data.query.QuerySortOrder.DEFAULT_COLLATOR;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nting.data.bean.BeanDescriptor;

import com.google.common.base.MoreObjects;

public class ListDataProvider<T> implements DataProvider<T> {

    private final List<T> backendCollection;
    private final BeanDescriptor<T> beanDescriptor;
    private final Function<T, Object> idFunction;

    public ListDataProvider(List<T> backendCollection, BeanDescriptor<T> beanDescriptor,
            Function<T, Object> idFunction) {
        this.backendCollection = backendCollection;
        this.beanDescriptor = beanDescriptor;
        this.idFunction = idFunction;
    }

    @Override
    public DataProviderPromise<Integer> size(Query<T> query) {
        Stream<T> stream = backendCollection.stream();

        if (query.queryFilter != null) {
            stream = stream.filter(query.queryFilter.toInMemoryFilter(beanDescriptor));
        }

        int size = (int) stream.count();
        return consumer -> consumer.accept(size);
    }

    @Override
    public DataProviderPromise<List<T>> fetch(Query<T> query) {
        Stream<T> stream = backendCollection.stream();

        if (query.queryFilter != null) {
            stream = stream.filter(query.queryFilter.toInMemoryFilter(beanDescriptor));
        }
        if (query.querySortOrder != null) {
            stream = stream.sorted(query.querySortOrder.toInMemorySortOrder(DEFAULT_COLLATOR, beanDescriptor));
        }
        stream = stream.skip(query.offset).limit(query.limit);

        List<T> fetchedData = stream.collect(Collectors.toList());
        return consumer -> consumer.accept(fetchedData);
    }

    @Override
    public BeanDescriptor<T> getBeanDescriptor() {
        return beanDescriptor;
    }

    @Override
    public Object getId(T item) {
        return idFunction.apply(item);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("backendCollection", backendCollection)
                .add("beanDescriptor", beanDescriptor).add("idFunction", idFunction).toString();
    }
}
