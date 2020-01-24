package org.nting.data.transform;

import java.util.Objects;
import java.util.function.Function;

public class SelectionTransform<T, R> implements Function<T, R> {

    private final T selectionToCheck;
    private final R selectedValue;
    private final R notSelectedValue;

    public SelectionTransform(T selectionToCheck, R selectedValue, R notSelectedValue) {
        this.selectionToCheck = selectionToCheck;
        this.selectedValue = selectedValue;
        this.notSelectedValue = notSelectedValue;
    }

    @Override
    public R apply(T value) {
        if (Objects.equals(selectionToCheck, value)) {
            return selectedValue;
        } else {
            return notSelectedValue;
        }
    }
}
