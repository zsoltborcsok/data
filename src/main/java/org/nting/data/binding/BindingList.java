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
        for (Binding binding : bindings) {
            binding.unbind();
            if (!(binding instanceof BindingList)) {
                bindings.remove(binding); // Only remove the 'single' bindings.
            }
        }

    }
}
