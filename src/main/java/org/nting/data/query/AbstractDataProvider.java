package org.nting.data.query;

import java.util.List;

import org.nting.data.Registration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class AbstractDataProvider<T> implements DataProvider<T> {

    private final List<DataProviderListener<T>> dataProviderListeners = Lists.newLinkedList();

    @Override
    public Registration addDataProviderListener(DataProviderListener<T> listener) {
        Preconditions.checkArgument(!dataProviderListeners.contains(listener));

        dataProviderListeners.add(listener);

        return () -> dataProviderListeners.remove(listener);
    }

    @Override
    public void refreshItem(T item) {
        fireDataChangeEvent(new DataChangeEvent.DataRefreshEvent<>(this, item));
    }

    @Override
    public void refreshAll() {
        fireDataChangeEvent(new DataChangeEvent<>(this));
    }

    private void fireDataChangeEvent(DataChangeEvent<T> dataChangeEvent) {
        dataProviderListeners.forEach(listener -> listener.dataChange(dataChangeEvent));
    }
}
