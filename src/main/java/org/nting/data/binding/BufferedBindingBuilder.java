package org.nting.data.binding;

import static org.nting.data.binding.Bindings.BindingStrategy.READ;
import static org.nting.data.binding.Bindings.BindingStrategy.READ_WRITE;

import org.nting.data.Property;
import org.nting.data.property.BufferedPropertySet;

import com.google.common.base.Converter;

public class BufferedBindingBuilder {

    private final BufferedPropertySet bufferedPropertySet = new BufferedPropertySet();

    public BufferedBindingBuilder() {
    }

    public BufferedPropertySet bufferedPropertySet() {
        return bufferedPropertySet;
    }

    public <F> OngoingBindingBuilder1<F> bind(Property<F> source) {
        return new OngoingBindingBuilder1<F>(source);
    }

    public class OngoingBindingBuilder1<F> {

        private final Property<F> source;

        public OngoingBindingBuilder1(Property<F> source) {
            this.source = source;
        }

        public <T> OngoingBindingBuilder2<F, T> using(Converter<F, T> converter) {
            return new OngoingBindingBuilder2<>(source, converter);
        }

        public Binding to(Property<F> target) {
            return Bindings.bind(READ_WRITE, bufferedPropertySet.createBufferedProperty(source), target);
        }

        public Binding from(Property<F> target) {
            return Bindings.bind(READ, target, bufferedPropertySet.createBufferedProperty(source));
        }
    }

    public class OngoingBindingBuilder2<F, T> {

        private final Property<F> source;
        private final Converter<F, T> converter;

        public OngoingBindingBuilder2(Property<F> source, Converter<F, T> converter) {
            this.source = source;
            this.converter = converter;
        }

        public Binding to(Property<T> target) {
            return Bindings.bind(READ_WRITE, bufferedPropertySet.createBufferedProperty(source), target, converter);
        }
    }
}
