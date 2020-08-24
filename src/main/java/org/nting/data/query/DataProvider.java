package org.nting.data.query;

import java.util.List;
import java.util.function.Consumer;

import org.nting.data.Registration;
import org.nting.data.bean.BeanDescriptor;

import com.google.common.base.Preconditions;

public interface DataProvider<T> {

    DataProviderPromise<Integer> size(Query<T> query);

    DataProviderPromise<List<T>> fetch(Query<T> query);

    BeanDescriptor<T> getBeanDescriptor();

    Registration addDataProviderListener(DataProviderListener<T> listener);

    void refreshItem(T item);

    void refreshAll();

    default Object getId(T item) {
        Preconditions.checkArgument(item != null);
        return item;
    }

    @FunctionalInterface
    interface DataProviderPromise<T> {

        void then(Consumer<T> consumer);
    }
}
