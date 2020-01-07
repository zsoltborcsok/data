package org.nting.data.property;

import java.util.List;

import org.nting.data.Buffered;
import org.nting.data.Property;

import com.google.common.collect.Lists;

public class BufferedPropertySet implements Buffered {

    private final List<BufferedProperty<?>> bufferedProperties = Lists.newArrayList();

    private boolean buffered = true;

    @SuppressWarnings("unchecked")
    public <F> BufferedProperty<F> createBufferedProperty(Property<F> source) {
        for (BufferedProperty<?> bufferedProperty : bufferedProperties) {
            if (source == bufferedProperty.getSource()) {
                return (BufferedProperty<F>) bufferedProperty;
            }
        }

        BufferedProperty<F> bufferedProperty = new BufferedProperty<>(source);
        bufferedProperties.add(bufferedProperty);
        bufferedProperty.setBuffered(buffered);
        return bufferedProperty;
    }

    @SuppressWarnings("unchecked")
    public <F> BufferedProperty<F> getBufferedProperty(Property<F> source) {
        return (BufferedProperty<F>) bufferedProperties.stream()
                .filter(bufferedProperty -> source == bufferedProperty.getSource()).findFirst().orElse(null);
    }

    public void addBufferedProperty(BufferedProperty<?> bufferedProperty) {
        if (!bufferedProperties.contains(bufferedProperty)) {
            bufferedProperties.add(bufferedProperty);
            bufferedProperty.setBuffered(buffered);
        }
    }

    public void removeBufferedProperty(BufferedProperty<?> bufferedProperty) {
        bufferedProperties.remove(bufferedProperty);
    }

    public void clear() {
        bufferedProperties.clear();
    }

    @Override
    public void commit() {
        bufferedProperties.forEach(BufferedProperty::commit);
    }

    @Override
    public void discard() {
        bufferedProperties.forEach(BufferedProperty::discard);
    }

    @Override
    public void setBuffered(boolean buffered) {
        if (this.buffered == buffered) {
            return;
        }

        this.buffered = buffered;
        bufferedProperties.forEach(bufferedProperty -> bufferedProperty.setBuffered(buffered));

        if (!buffered) {
            commit();
        }
    }

    @Override
    public boolean isBuffered() {
        return buffered;
    }

    @Override
    public boolean isModified() {
        for (BufferedProperty<?> bufferedProperty : bufferedProperties) {
            if (bufferedProperty.isModified()) {
                return true;
            }
        }

        return false;
    }
}
