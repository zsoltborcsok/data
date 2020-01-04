package org.nting.data.binding;

import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;

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
        } else {
            return new AutoBindingReadWrite<>(source, target);
        }
    }

    public static <F, T> Binding bind(BindingStrategy strategy, Property<F> source, Property<T> target,
            Converter<F, T> converter) {
        if (strategy == BindingStrategy.READ) {
            return new AutoBindingRead<>(source, target, converter);
        } else {
            return new AutoBindingReadWrite<>(source, target, converter);
        }
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
}
