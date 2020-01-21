package org.nting.data.inject;

import org.nting.data.bean.RuntimeBean;

public interface Injector {

    void injectProperties(RuntimeBean instance);

    void injectProperties(RuntimeBean instance, String injectionName);

    <T> T getPropertyValue(String injectionName, Object key);
}
