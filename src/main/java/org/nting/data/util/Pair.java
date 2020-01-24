package org.nting.data.util;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class Pair<FIRST, SECOND> {

    public final FIRST first;
    public final SECOND second;

    public static <FIRST, SECOND> Pair<FIRST, SECOND> of(FIRST first, SECOND second) {
        return new Pair<>(first, second);
    }

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("first", first).add("second", second).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}