package org.nting.data.query;

import com.google.common.base.MoreObjects;

public class Query<T> {

    public static final int DEFAULT_LIMIT = 100;

    public final int offset;
    public final int limit;
    public final QuerySortOrder querySortOrder;
    public final QueryFilter queryFilter;

    public Query(QuerySortOrder querySortOrder, QueryFilter queryFilter) {
        this(0, DEFAULT_LIMIT, querySortOrder, queryFilter);
    }

    public Query(int offset, int limit, QuerySortOrder querySortOrder, QueryFilter queryFilter) {
        this.offset = offset;
        this.limit = limit;
        this.querySortOrder = querySortOrder;
        this.queryFilter = queryFilter;
    }

    public Query<T> withNextPage() {
        return new Query<>(offset + limit, limit, querySortOrder, queryFilter);
    }

    public Query<T> withMoreItems(int moreItems) {
        return new Query<>(offset, limit + moreItems, querySortOrder, queryFilter);
    }

    public Query<T> withMoreItems() {
        return withMoreItems(DEFAULT_LIMIT);
    }

    public Query<T> withSortOrder(QuerySortOrder querySortOrder) {
        return new Query<>(offset, limit, querySortOrder, queryFilter);
    }

    public Query<T> withFilter(QueryFilter queryFilter) {
        return new Query<>(offset, limit, querySortOrder, queryFilter);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("offset", offset).add("limit", limit) //
                .add("querySortOrder", querySortOrder).add("queryFilter", queryFilter) //
                .toString();
    }
}
