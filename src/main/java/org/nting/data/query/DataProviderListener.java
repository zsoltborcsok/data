package org.nting.data.query;

@FunctionalInterface
public interface DataProviderListener<T> {

    void dataChange(DataChangeEvent<T> event);
}
