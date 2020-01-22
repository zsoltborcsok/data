package org.nting.data.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.nting.data.inject.Provider.provider;
import static org.nting.data.model.IssuePropertySet.Properties.ID;
import static org.nting.data.model.IssuePropertySet.Properties.STATUS;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Status.NOT_STARTED;

import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.nting.data.model.IssueBean;
import org.nting.data.model.IssuePropertySet;
import org.nting.data.model.Status;
import org.nting.data.property.PropertySet;
import org.nting.data.property.PropertySetBuilder;

public class InjectTest {

    public static class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            toType(IssuePropertySet.class).bind(STATUS, Status.DEFERRED);
            toType(IssuePropertySet.class).bind(ID, provider(() -> UUID.randomUUID().toString()));

            toName("XYZ").bind("value", "xyz");

            toType(IssueBean.class).bind("title", "[TITLE]");
        }
    }

    private Injector injector;

    @Before
    public void setUp() {
        injector = InjectorFactory.createInjector(new TestModule());
    }

    @Test
    public void testInject_ToType() {
        IssuePropertySet issue = new IssuePropertySet();
        assertNotEquals(Status.DEFERRED, issue.getValue(STATUS));
        String lastId = issue.getValue(ID);

        injector.injectProperties(issue);

        assertEquals(Status.DEFERRED, issue.getValue(STATUS));
        assertNotEquals(lastId, issue.getValue(ID));

        // Always gets a unique ID by a Provider
        lastId = issue.getValue(ID);
        injector.injectProperties(issue);
        assertNotEquals(lastId, issue.getValue(ID));
    }

    @Test
    public void testInject_ToBean() {
        IssueBean issue = new IssueBean("", null, Collections.emptyList(), NOT_STARTED, BLOCKER);

        injector.injectProperties(IssueBean.beanDescriptor().createRuntimeBean(issue));

        assertEquals("[TITLE]", issue.getTitle());
    }

    @Test
    public void testInject_ToName() {
        PropertySet propertySet = new PropertySetBuilder().addObjectProperty("value", "").build();

        injector.injectProperties(propertySet, "XYZ");

        assertEquals("xyz", propertySet.getValue("value"));
    }
}
