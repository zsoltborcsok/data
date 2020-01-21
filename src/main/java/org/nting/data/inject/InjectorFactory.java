package org.nting.data.inject;

import org.nting.data.inject.internal.BinderImpl;
import org.nting.data.inject.internal.InjectorImpl;

import com.google.common.collect.Lists;

public class InjectorFactory {

    public static Injector createInjector(Module... modules) {
        return createInjector(Lists.newArrayList(modules));
    }

    public static Injector createInjector(Iterable<? extends Module> modules) {
        BinderImpl binder = new BinderImpl();
        for (Module module : modules) {
            module.configure(binder);
        }

        return new InjectorImpl(binder);
    }

    private InjectorFactory() {
    }
}
