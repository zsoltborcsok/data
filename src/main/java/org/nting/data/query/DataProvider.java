package org.nting.data.query;

import java.util.Collection;
import java.util.function.Consumer;

import org.nting.data.bean.BeanDescriptor;

import com.google.common.base.Preconditions;

public interface DataProvider<T> {

    DataProviderPromise<Integer> size(Query<T> query);

    DataProviderPromise<Collection<T>> fetch(Query<T> query);

    BeanDescriptor<T> getBeanDescriptor();

    default Object getId(T item) {
        Preconditions.checkArgument(item != null);
        return item;
    }

    @FunctionalInterface
    interface DataProviderPromise<T> {

        void then(Consumer<T> consumer);
    }
}
