package org.nting.data.model;

import java.util.Collections;
import java.util.List;

import org.nting.data.bean.BeanDescriptor;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class IssueBean {

    private String title;
    private String description;
    private List<String> versions = Lists.newLinkedList();
    private Status status;
    private Priority priority;

    public IssueBean(String title, String description, List<String> versions, Status status, Priority priority) {
        this.title = title;
        this.description = description;
        this.versions.addAll(versions);
        this.status = status;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getVersions() {
        return Collections.unmodifiableList(versions);
    }

    public void setVersions(List<String> versions) {
        this.versions.clear();
        this.versions.addAll(versions);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("title", title).add("description", description)
                .add("version", versions).add("status", status).add("priority", priority).toString();
    }

    public static BeanDescriptor<IssueBean> beanDescriptor() {
        BeanDescriptor<IssueBean> beanDescriptor = new BeanDescriptor<>(IssueBean.class);
        beanDescriptor.addPropertyDescriptor("title", IssueBean::getTitle, IssueBean::setTitle);
        beanDescriptor.addPropertyDescriptor("description", IssueBean::getDescription, IssueBean::setDescription);
        beanDescriptor.addPropertyDescriptor("version", IssueBean::getVersions, IssueBean::setVersions);
        beanDescriptor.addPropertyDescriptor("status", IssueBean::getStatus, IssueBean::setStatus);
        beanDescriptor.addPropertyDescriptor("priority", IssueBean::getPriority, IssueBean::setPriority);

        return beanDescriptor;

    }
}
