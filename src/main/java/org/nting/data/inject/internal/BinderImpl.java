package org.nting.data.inject.internal;

import java.util.Map;

import org.nting.data.inject.Binder;

import com.google.common.collect.Maps;

public class BinderImpl implements Binder {

    private final Map<String, Map<Object, Object>> binderMap = Maps.newHashMap();

    @Override
    public void requestInjection(String injectionName, Object propertyId, Object value) {
        binderMap.computeIfAbsent(injectionName, k -> Maps.newLinkedHashMap()).put(propertyId, value);
    }

    @Override
    public Map<Object, Object> getInjections(String injectionName) {
        return binderMap.get(injectionName);
    }
}
