package org.nting.data.inject;

import java.util.Map;

public interface Binder {

    void requestInjection(String injectionName, Object propertyId, Object value);

    Map<Object, Object> getInjections(String injectionName);
}
