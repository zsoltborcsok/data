package org.nting.data.inject.internal;

import java.util.Map;

import org.nting.data.Property;
import org.nting.data.bean.RuntimeBean;
import org.nting.data.inject.Binder;
import org.nting.data.inject.Injector;
import org.nting.data.inject.Provider;

public class InjectorImpl implements Injector {

    private final Binder binder;

    public InjectorImpl(Binder binder) {
        this.binder = binder;
    }

    @Override
    public void injectProperties(RuntimeBean instance) {
        injectProperties(instance, instance.getClass().getName());
        // for (Class aClass : collectSuperClasses(instance.getClass())) {
        // injectProperties(instance, aClass.getName());
        // }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void injectProperties(RuntimeBean instance, String injectionName) {
        Map<Object, Object> injections = binder.getInjections(injectionName);
        if (injections != null) {
            for (Object propertyId : injections.keySet()) {
                Property property = instance.getProperty(propertyId);
                if (property != null) {
                    Object value = injections.get(propertyId);
                    if (value instanceof Provider) {
                        value = ((Provider) value).get();
                    }
                    property.setValue(value);
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> T getPropertyValue(String injectionName, Object key) {
        Map<Object, Object> injections = binder.getInjections(injectionName);
        if (injections != null) {
            Object value = injections.get(key);
            if (value instanceof Provider) {
                value = ((Provider) value).get();
            }
            return (T) value;
        }

        return null;
    }
}
