package org.nting.data.query;

import java.util.Comparator;

import org.nting.data.bean.BeanDescriptor;

import com.google.common.base.MoreObjects;

public class QuerySortOrder {

    public static final Comparator<String> DEFAULT_COLLATOR = Comparator.nullsFirst(String::compareTo);

    public final String sortedBy;
    public final boolean ascending;
    public final QuerySortOrder next;

    public QuerySortOrder(String sortedBy, boolean ascending) {
        this(sortedBy, ascending, null);
    }

    public QuerySortOrder(String sortedBy, boolean ascending, QuerySortOrder next) {
        this.sortedBy = sortedBy;
        this.ascending = ascending;
        this.next = next;
    }

    /** Works only for properties with Comparable type. */
    public <T> Comparator<T> toInMemorySortOrder(Comparator<String> collator, BeanDescriptor<T> beanDescriptor) {
        Comparator<T> comparator = Comparator.comparing(beanDescriptor.getPropertyDescriptor(sortedBy).getter,
                new AnyTypeComparator(collator, ascending));
        if (next != null) {
            comparator = comparator.thenComparing(next.toInMemorySortOrder(collator, beanDescriptor));
        }

        return comparator;
    }

    public QuerySortOrder withNext(QuerySortOrder next) {
        return new QuerySortOrder(sortedBy, ascending, next);
    }

    public QuerySortOrder withOppositeOrder() {
        return new QuerySortOrder(sortedBy, !ascending, next);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("sortedBy", sortedBy).add("ascending", ascending).add("next", next)
                .toString();
    }

    /** The objects are expected to be Comparable-s... */
    private static class AnyTypeComparator implements Comparator<Object> {

        private final Comparator<String> collator;
        private final boolean ascending;

        private AnyTypeComparator(Comparator<String> collator, boolean ascending) {
            this.collator = collator;
            this.ascending = ascending;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare(Object value1, Object value2) {
            if (value1 instanceof String && value2 instanceof String) {
                return ascending ? collator.compare((String) value1, (String) value2)
                        : collator.compare((String) value2, (String) value1);
            } else {
                if (value1 == null && value2 != null) {
                    return ascending ? -1 : 1;
                } else if (value1 != null && value2 == null) {
                    return ascending ? 1 : -1;
                } else if (value1 == null) { // && value2 == null
                    return 0;
                }

                return ascending ? ((Comparable) value1).compareTo(value2) : ((Comparable) value2).compareTo(value1);
            }
        }
    }
}
