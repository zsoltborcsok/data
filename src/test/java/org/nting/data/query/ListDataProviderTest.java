package org.nting.data.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Status.IN_PROGRESS;
import static org.nting.data.model.Status.NOT_STARTED;
import static org.nting.data.query.filter.Compare.equal;
import static org.nting.data.query.filter.Compare.greaterOrEqual;
import static org.nting.data.query.filter.Compare.lessThan;
import static org.nting.data.query.filter.Compare.notEqual;
import static org.nting.data.query.filter.Compare.startsWith;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nting.data.model.IssueBean;
import org.nting.data.model.Priority;
import org.nting.data.model.Status;
import org.nting.data.query.filter.And;
import org.nting.data.query.filter.Compare;
import org.nting.data.query.filter.Or;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ListDataProviderTest {

    private ListDataProvider<IssueBean> issueDataProvider;

    @Before
    public void setUp() {
        List<IssueBean> issues = Lists.newLinkedList();
        for (int i = 0; i < 50; i++) {
            IssueBean issueBean = new IssueBean("title@" + i, "description@" + i, ImmutableList.of(i + ".0.0"),
                    Status.values()[i % 4], Priority.values()[i % 5]);
            issueBean.setId(String.valueOf(i));
            issues.add(issueBean);
        }
        issueDataProvider = new ListDataProvider<>(issues, IssueBean.beanDescriptor(), IssueBean::getId);
    }

    @Test
    public void testFetch_Filter() {
        issueDataProvider.fetch(new Query<>(null, equal("status", NOT_STARTED))).then(issues -> {
            assertEquals(13, issues.size());
            issues.forEach(issue -> assertEquals(NOT_STARTED, issue.getStatus()));
        });

        issueDataProvider
                .fetch(new Query<>(null, new Or(startsWith("title", "title@1"), startsWith("title", "title@2"))))
                .then(issues -> {
                    assertEquals(22, issues.size());
                    issues.forEach(issue -> assertTrue(
                            issue.getTitle().startsWith("title@1") || issue.getTitle().startsWith("title@2")));
                });

        issueDataProvider.fetch(new Query<>(null, new And(notEqual("priority", BLOCKER), equal("status", IN_PROGRESS))))
                .then(issues -> {
                    assertEquals(10, issues.size());
                    issues.forEach(issue -> {
                        assertNotEquals(BLOCKER, issue.getPriority());
                        assertEquals(IN_PROGRESS, issue.getStatus());
                    });
                });

        issueDataProvider.fetch(new Query<>(null, greaterOrEqual("description", "description@3"))).then(issues -> {
            assertEquals(27, issues.size());
            issues.forEach(issue -> assertFalse(issue.getDescription().startsWith("description@0")
                    || issue.getDescription().startsWith("description@1")
                    || issue.getDescription().startsWith("description@2")));
        });

        issueDataProvider.fetch(new Query<>(null, lessThan("description", "description@3"))).then(issues -> {
            assertEquals(23, issues.size());
            issues.forEach(issue -> assertTrue(issue.getDescription().startsWith("description@0")
                    || issue.getDescription().startsWith("description@1")
                    || issue.getDescription().startsWith("description@2")));
        });
    }

    @Test
    public void testSize_Filter() {
        issueDataProvider.size(new Query<>(null, equal("status", NOT_STARTED)))
                .then(size -> assertEquals((Integer) 13, size));

        issueDataProvider
                .size(new Query<>(null, new Or(startsWith("title", "title@1"), startsWith("title", "title@2"))))
                .then(size -> assertEquals((Integer) 22, size));

        issueDataProvider.size(new Query<>(null, new And(notEqual("priority", BLOCKER), equal("status", IN_PROGRESS))))
                .then(size -> assertEquals((Integer) 10, size));

        issueDataProvider.size(new Query<>(null, greaterOrEqual("description", "description@3")))
                .then(size -> assertEquals((Integer) 27, size));

        issueDataProvider.size(new Query<>(null, lessThan("description", "description@3")))
                .then(size -> assertEquals((Integer) 23, size));
    }

    @Test
    public void testFetch_Sort() {
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);
        Compare filter = equal("priority", BLOCKER);

        issueDataProvider.fetch(new Query<>(sortOrder, filter)).then(issues -> {
            assertEquals(10, issues.size());
            for (int i = 0; i < issues.size() - 1; i++) {
                assertTrue(issues.get(i).getStatus().ordinal() <= issues.get(i + 1).getStatus().ordinal());
            }
        });

        issueDataProvider.fetch(new Query<>(sortOrder.withOppositeOrder(), filter)).then(issues -> {
            assertEquals(10, issues.size());
            for (int i = 0; i < issues.size() - 1; i++) {
                assertTrue(issues.get(i).getStatus().ordinal() >= issues.get(i + 1).getStatus().ordinal());
            }
        });

        issueDataProvider.fetch(new Query<>(sortOrder.withNext(new QuerySortOrder("title", false)), filter))
                .then(issues -> {
                    assertEquals(10, issues.size());
                    for (int i = 0; i < issues.size() - 1; i++) {
                        IssueBean issue = issues.get(i);
                        IssueBean nextIssue = issues.get(i + 1);
                        assertTrue(issue.getStatus().ordinal() < nextIssue.getStatus().ordinal()
                                || (issue.getStatus() == nextIssue.getStatus()
                                        && 0 <= issue.getTitle().compareTo(nextIssue.getTitle())));
                    }
                });
    }

    @Test
    public void testSize_Sort() {
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);
        Compare filter = greaterOrEqual("description", "description@3");

        // Size does not affected by the sort order
        issueDataProvider.size(new Query<>(sortOrder, filter)).then(size -> assertEquals((Integer) 27, size));

    }

    @Test
    public void testFetch_Limit() {
        Compare filter = equal("priority", BLOCKER);
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);

        issueDataProvider.fetch(new Query<>(0, 5, sortOrder, filter)).then(issues -> {
            assertEquals(5, issues.size());
            for (int i = 0; i < issues.size() - 1; i++) {
                assertTrue(issues.get(i).getStatus().ordinal() <= issues.get(i + 1).getStatus().ordinal());
            }
        });
    }

    @Test
    public void testSize_Limit() {
        Compare filter = equal("priority", BLOCKER);
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);

        // Size does not consider limit
        issueDataProvider.size(new Query<>(0, 5, sortOrder, filter)).then(size -> assertEquals((Integer) 10, size));
    }

    @Test
    public void testFetch_Offset() {
        Compare filter = equal("priority", BLOCKER);
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);

        List<IssueBean> filteredIssues = Lists.newLinkedList();
        List<IssueBean> filteredPagedIssues = Lists.newLinkedList();

        issueDataProvider.fetch(new Query<>(sortOrder, filter)).then(filteredIssues::addAll);

        issueDataProvider.fetch(new Query<>(0, 4, sortOrder, filter)).then(filteredPagedIssues::addAll);
        issueDataProvider.fetch(new Query<>(4, 4, sortOrder, filter)).then(filteredPagedIssues::addAll);
        issueDataProvider.fetch(new Query<>(8, 4, sortOrder, filter)).then(filteredPagedIssues::addAll);

        assertEquals(filteredIssues, filteredPagedIssues);
    }

    @Test
    public void testSize_Offset() {
        QuerySortOrder sortOrder = new QuerySortOrder("status", true);
        Compare filter = greaterOrEqual("description", "description@3");

        // Size does not consider offset either
        issueDataProvider.size(new Query<>(10, 10, sortOrder, filter)).then(size -> assertEquals((Integer) 27, size));
    }
}