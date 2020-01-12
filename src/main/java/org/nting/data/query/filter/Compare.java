package org.nting.data.query.filter;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.nting.data.bean.BeanDescriptor;
import org.nting.data.query.QueryFilter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

public class Compare implements QueryFilter {

    public enum Operation {
        EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL, EXIST, //
        POINTER_EQUAL, POINTER_NOT_EQUAL, STARTS_WITH, STARTS_WITH_IGNORE_CASE
    }

    private final String propertyName;
    private final Operation operation;
    private final Object value;

    private Compare(String propertyName, Object value, Operation operation) {
        this.propertyName = propertyName;
        this.value = value;
        this.operation = operation;
    }

    /** Works only for properties with Comparable type. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Predicate<T> toInMemoryFilter(BeanDescriptor<T> beanDescriptor) {
        Function<T, Comparable> getter = beanDescriptor.<Comparable> getPropertyDescriptor(propertyName).getter;
        Comparator beanComparator = Comparator.nullsFirst(Comparator.comparing(getter));
        switch (operation) {
        case EQUAL:
        case POINTER_EQUAL:
            return bean -> Objects.equals(value, getter.apply(bean));
        case NOT_EQUAL:
            return bean -> beanComparator.compare(value, bean) != 0;
        case GREATER:
            return bean -> beanComparator.compare(value, bean) > 0;
        case LESS:
            return bean -> beanComparator.compare(value, bean) < 0;
        case GREATER_OR_EQUAL:
            return bean -> beanComparator.compare(value, bean) >= 0;
        case LESS_OR_EQUAL:
            return bean -> beanComparator.compare(value, bean) <= 0;
        case EXIST:
            return bean -> Objects.nonNull(getter.apply(bean));
        case STARTS_WITH:
            return bean -> {
                Object propertyValue = getter.apply(bean);
                return propertyValue instanceof String && ((String) propertyValue).startsWith((String) value);
            };
        case STARTS_WITH_IGNORE_CASE:
            return bean -> {
                Object propertyValue = getter.apply(bean);
                return propertyValue instanceof String && ((String) propertyValue).toLowerCase()
                        .startsWith(Strings.nullToEmpty((String) value).toLowerCase());
            };
        default:
            return propertyValue -> false;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("propertyName", propertyName).add("operation", operation)
                .add("value", value).toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int compareValue(Object propertyValue) {
        if (null == value) {
            return null == propertyValue ? 0 : -1;
        } else if (null == propertyValue) {
            return 1;
        } else if (value instanceof Comparable) {
            return -((Comparable) value).compareTo(propertyValue);
        }

        throw new IllegalArgumentException();
    }

    public static Compare equal(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.EQUAL);
    }

    public static Compare notEqual(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.NOT_EQUAL);
    }

    public static Compare greaterThan(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.GREATER);
    }

    public static Compare lessThan(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.LESS);
    }

    public static Compare greaterOrEqual(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.GREATER_OR_EQUAL);
    }

    public static Compare lessOrEqual(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.LESS_OR_EQUAL);
    }

    public static Compare startsWith(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.STARTS_WITH);
    }

    public static Compare startsWithIgnoreCase(String propertyName, Object value) {
        return new Compare(propertyName, value, Operation.STARTS_WITH_IGNORE_CASE);
    }

    public static Compare pointerEqual(String propertyName, Object pointer) {
        return new Compare(propertyName, pointer, Operation.POINTER_EQUAL);
    }

    public static Compare pointerNotEqual(String propertyName, Object pointer) {
        return new Compare(propertyName, pointer, Operation.POINTER_NOT_EQUAL);
    }

    public static Compare exist(String propertyName, boolean value) {
        return new Compare(propertyName, value, Operation.EXIST);
    }

    public static Compare compare(String propertyName, Object value, String operation) {
        return new Compare(propertyName, value, Operation.valueOf(operation));
    }
}
