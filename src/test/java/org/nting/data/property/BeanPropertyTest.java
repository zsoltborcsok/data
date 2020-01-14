package org.nting.data.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.nting.data.model.Status.IN_PROGRESS;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.IssueBean;
import org.nting.data.model.Priority;
import org.nting.data.model.Status;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class BeanPropertyTest {

    private IssueBean issueBean = new IssueBean("tit.le", "descript.ion", ImmutableList.of("1.0.0", "1.1.0"),
            Status.NOT_STARTED, Priority.MAJOR);

    @Mock
    private ValueChangeListener<String> valueChangeListener;

    @Test
    public void testStringProperty() {
        BeanProperty<IssueBean, String> beanProperty = new BeanProperty<>(issueBean, IssueBean::getTitle,
                IssueBean::setTitle);

        beanProperty.setValue("H.E.L.P.");
        assertEquals("H.E.L.P.", beanProperty.getValue());
        assertEquals("H.E.L.P.", issueBean.getTitle());

        beanProperty.setValue(null);
        assertNull(beanProperty.getValue());
        assertNull(issueBean.getTitle());
    }

    @Test
    public void testEnumProperty() {
        BeanProperty<IssueBean, Status> beanProperty = new BeanProperty<>(issueBean, IssueBean::getStatus,
                IssueBean::setStatus);

        beanProperty.setValue(IN_PROGRESS);
        assertEquals(IN_PROGRESS, beanProperty.getValue());
        assertEquals(IN_PROGRESS, issueBean.getStatus());
    }

    @Test
    public void testListProperty() {
        BeanProperty<IssueBean, List<String>> beanProperty = new BeanProperty<>(issueBean, IssueBean::getVersions,
                IssueBean::setVersions);

        beanProperty.setValue(
                Stream.concat(beanProperty.getValue().stream(), Stream.of("1.2.0")).collect(Collectors.toList()));
        assertTrue(beanProperty.getValue().contains("1.2.0"));
        assertEquals(ImmutableList.of("1.0.0", "1.1.0", "1.2.0"), issueBean.getVersions());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValueChangeNotification() {
        BeanProperty<IssueBean, String> beanProperty = new BeanProperty<>(issueBean, IssueBean::getTitle,
                IssueBean::setTitle);
        beanProperty.addValueChangeListener(valueChangeListener);

        beanProperty.setValue("H.E.L.P.");

        ArgumentCaptor<ValueChangeEvent<String>> valueChangeEvent = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueChangeListener).valueChange(valueChangeEvent.capture());
        assertEquals("tit.le", valueChangeEvent.getValue().getPrevValue());
        assertEquals("H.E.L.P.", valueChangeEvent.getValue().getValue());
        assertSame(beanProperty, valueChangeEvent.getValue().getProperty());
    }
}