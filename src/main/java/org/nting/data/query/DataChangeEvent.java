package org.nting.data.query;

import java.util.EventObject;

import com.google.common.base.Preconditions;

public class DataChangeEvent<T> extends EventObject {

    public DataChangeEvent(DataProvider<T> source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataProvider<T> getSource() {
        return (DataProvider<T>) super.getSource();
    }

    public static class DataRefreshEvent<T> extends DataChangeEvent<T> {

        private final T item;

        public DataRefreshEvent(DataProvider<T> source, T item) {
            super(source);

            Preconditions.checkArgument(item != null, "Item can not be null");
            this.item = item;
        }

        public T getItem() {
            return item;
        }
    }
}
