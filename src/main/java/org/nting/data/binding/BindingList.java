package org.nting.data.binding;

import java.util.List;

import com.google.common.collect.Lists;

public class BindingList implements Binding {

    private final List<Binding> bindings = Lists.newLinkedList();

    public void addBinding(Binding binding) {
        bindings.add(binding);
    }

    @Override
    public void unbind() {
        bindings.forEach(Binding::unbind);
        bindings.removeIf(binding -> !(binding instanceof BindingList)); // Only remove the 'single' bindings.
    }
}
