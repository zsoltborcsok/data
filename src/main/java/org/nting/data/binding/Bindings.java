package org.nting.data.binding;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.condition.Condition;

import com.google.common.base.Converter;

public class Bindings {

    public static <T> ValueChangeListener<T> emptyValueChangeListener() {
        return event -> {
        };
    }

    public enum BindingStrategy {
        READ, READ_WRITE
    }

    public static <T> Binding bind(BindingStrategy strategy, Property<T> source, Property<T> target) {
        if (strategy == BindingStrategy.READ) {
            return new AutoBindingRead<>(source, target);
        } else if (strategy == BindingStrategy.READ_WRITE) {
            return new AutoBindingReadWrite<>(source, target);
        } else {
            throw new IllegalArgumentException("Unknown BindingStrategy.");
        }
    }

    public static <F, T> Binding bind(BindingStrategy strategy, Property<F> source, Property<T> target,
            Converter<F, T> converter) {
        if (strategy == BindingStrategy.READ) {
            return new AutoBindingRead<>(source, target, converter);
        } else if (strategy == BindingStrategy.READ_WRITE) {
            return new AutoBindingReadWrite<>(source, target, converter);
        } else {
            throw new IllegalArgumentException("Unknown BindingStrategy.");
        }
    }

    public static Binding bindCondition(Condition condition, Property<Boolean> target) {
        return new AutoBindingRead<>(condition, target);
    }

    /** It's a read binding. */
    public static <T> Binding conditionalBinding(Property<T> source, Property<T> target, Property<Boolean> condition) {
        return new ConditionalAutoBinding<>(source, target, condition);
    }

    /** It's a read binding. */
    public static <F, T> Binding conditionalBinding(Property<F> source, Property<T> target, Property<Boolean> condition,
            Converter<F, T> converter) {
        return new ConditionalAutoBinding<>(source, target, condition, converter);
    }

    public static Binding asBinding(Registration registration) {
        return registration::remove;
    }

    /**
     * By 'welding' we add an empty ValueChangeListener to the given property, which results ValueChangeListener
     * registration on the dependent properties. It could be useful in such cases when the property value isn't
     * dynamically calculated in the getValue() method because of performance reasons, and needs to provide the value
     * without any listener. With the 'weld' terminology wanted to emphasize that the property and the dependent
     * properties will have the same lifecycle as they are referencing to each other.
     */
    public static <T> Property<T> weld(Property<T> property) {
        property.addValueChangeListener(emptyValueChangeListener());
        return property;
    }

    private static abstract class AutoBindingBase<F, T> implements Binding {

        protected final Property<F> source;
        protected final Property<T> target;
        protected final Converter<F, T> converter;

        public AutoBindingBase(Property<F> source, Property<T> target) {
            this(source, target, null);
        }

        public AutoBindingBase(Property<F> source, Property<T> target, Converter<F, T> converter) {
            this.source = source;
            this.target = target;
            this.converter = converter;

            bind();
        }

        protected abstract void bind();
    }

    private static class AutoBindingRead<F, T> extends AutoBindingBase<F, T> {

        private Registration registration;

        public AutoBindingRead(Property<F> source, Property<T> target) {
            super(source, target);
        }

        public AutoBindingRead(Property<F> source, Property<T> target, Converter<F, T> converter) {
            super(source, target, converter);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void bind() {
            ValueChangeListener<F> valueChangeHandler = event -> {
                if (converter == null) {
                    target.setValue((T) source.getValue());
                } else {
                    target.setValue(converter.convert(source.getValue()));
                }
            };

            valueChangeHandler.valueChange(new ValueChangeEvent<>(source, null));
            registration = source.addValueChangeListener(valueChangeHandler);
        }

        @Override
        public void unbind() {
            if (registration != null) {
                registration.remove();
                registration = null;
            }
        }
    }

    private static class AutoBindingReadWrite<F, T> extends AutoBindingBase<F, T> {

        private Registration sourceRegistration;
        private Registration targetRegistration;

        public AutoBindingReadWrite(Property<F> source, Property<T> target) {
            super(source, target);
        }

        public AutoBindingReadWrite(Property<F> source, Property<T> target, Converter<F, T> converter) {
            super(source, target, converter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void bind() {
            ValueChangeListener<F> sourceValueChangeHandler = event -> {
                if (converter == null) {
                    target.setValue((T) source.getValue());
                } else {
                    target.setValue(converter.convert(source.getValue()));
                }
            };

            sourceValueChangeHandler.valueChange(new ValueChangeEvent<>(source, null));
            sourceRegistration = source.addValueChangeListener(sourceValueChangeHandler);
            targetRegistration = target.addValueChangeListener(event -> {
                if (converter == null) {
                    source.setValue((F) target.getValue());
                } else {
                    source.setValue(converter.reverse().convert(target.getValue()));
                }
            });
        }

        @Override
        public void unbind() {
            if (sourceRegistration != null) {
                sourceRegistration.remove();
                sourceRegistration = null;
            }
            if (targetRegistration != null) {
                targetRegistration.remove();
                targetRegistration = null;
            }
        }
    }

    private static class ConditionalAutoBinding<F, T> implements Binding {

        private final Property<F> source;
        private final Property<T> target;
        private final Converter<F, T> converter;
        private final Property<Boolean> condition;

        private Registration sourceRegistration;
        private Registration conditionRegistration;

        public ConditionalAutoBinding(Property<F> source, Property<T> target, Property<Boolean> condition) {
            this(source, target, condition, null);
        }

        public ConditionalAutoBinding(Property<F> source, Property<T> target, Property<Boolean> condition,
                Converter<F, T> converter) {
            this.source = source;
            this.target = target;
            this.converter = converter;
            this.condition = condition;

            bind();
        }

        @SuppressWarnings("unchecked")
        private void bind() {
            ValueChangeListener<F> valueChangeHandler = event -> {
                if (condition.getValue()) {
                    if (converter == null) {
                        target.setValue((T) source.getValue());
                    } else {
                        target.setValue(converter.convert(source.getValue()));
                    }
                }
            };

            valueChangeHandler.valueChange(new ValueChangeEvent<>(source, null));
            sourceRegistration = source.addValueChangeListener(valueChangeHandler);
            conditionRegistration = condition.addValueChangeListener(e -> valueChangeHandler.valueChange(null));
        }

        @Override
        public void unbind() {
            if (sourceRegistration != null) {
                sourceRegistration.remove();
                sourceRegistration = null;
            }
            if (conditionRegistration != null) {
                conditionRegistration.remove();
                conditionRegistration = null;
            }
        }
    }
}
