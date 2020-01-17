package org.nting.data.model;

import static org.nting.data.model.IssuePropertySet.Properties.DESCRIPTION;
import static org.nting.data.model.IssuePropertySet.Properties.ID;
import static org.nting.data.model.IssuePropertySet.Properties.PRIORITY;
import static org.nting.data.model.IssuePropertySet.Properties.STATUS;
import static org.nting.data.model.IssuePropertySet.Properties.TITLE;
import static org.nting.data.model.IssuePropertySet.Properties.VERSIONS;
import static org.nting.data.model.Priority.BLOCKER;
import static org.nting.data.model.Status.NOT_STARTED;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.nting.data.property.PropertySet;

public class IssuePropertySet extends PropertySet {

    public enum Properties {
        ID, TITLE, DESCRIPTION, VERSIONS, STATUS, PRIORITY
    }

    public IssuePropertySet() {
        this("", null, Collections.emptyList());
    }

    public IssuePropertySet(String title, String description, List<String> versions) {
        this(title, description, versions, NOT_STARTED, BLOCKER);
    }

    public IssuePropertySet(String title, String description, List<String> versions, Status status, Priority priority) {
        addObjectProperty(ID, UUID.randomUUID().toString());
        addObjectProperty(TITLE, title);
        addObjectProperty(DESCRIPTION, description);
        addListProperty(VERSIONS, versions);
        addObjectProperty(STATUS, status);
        addObjectProperty(PRIORITY, priority);
    }
}
