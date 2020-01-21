package org.nting.data.inject;

public abstract class AbstractModule implements Module {

    private Binder binder;

    @Override
    public void configure(Binder binder) {
        this.binder = binder;
        configure();
    }

    protected abstract void configure();

    protected PropertyInjector toType(Class<?> type) {
        return toName(type.getName());
    }

    protected PropertyInjector toName(String name) {
        return new PropertyInjector(name);
    }

    public class PropertyInjector {

        private final String injectionName;

        public PropertyInjector(String injectionName) {
            this.injectionName = injectionName;
        }

        public void bind(Object propertyId, Object value) {
            binder.requestInjection(injectionName, propertyId, value);
        }

        public void bind(Object propertyId, Provider<?> provider) {
            binder.requestInjection(injectionName, propertyId, provider);
        }
    }
}
