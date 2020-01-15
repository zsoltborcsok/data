package org.nting.data.binding;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nting.data.Property;
import org.nting.data.condition.Conditions;
import org.nting.data.property.ObjectProperty;

public class ConditionBindingsTest {

    @Test
    public void testTextCondition() {
        Property<String> state = new ObjectProperty<>("LOADING");
        Property<Boolean> visible = new ObjectProperty<>(false);

        Bindings.bindCondition(Conditions.textOf(state).equals("LOADING", false), visible);
        assertTrue(visible.getValue());

        state.setValue("LOADED");
        assertFalse(visible.getValue());

        state.setValue("loading");
        assertFalse(visible.getValue());
    }

    @Test
    public void testTextCondition_IgnoreCase() {
        Property<String> state = new ObjectProperty<>("");
        Property<Boolean> visible = new ObjectProperty<>(false);

        Bindings.bindCondition(Conditions.textOf(state).equals("LOADING", true), visible);
        assertFalse(visible.getValue());

        state.setValue("loading");
        assertTrue(visible.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOrCondition() {
        Property<String> state = new ObjectProperty<>("");
        Property<Boolean> visible = new ObjectProperty<>(false);

        Bindings.bindCondition(
                Conditions.textOf(state).equals("LOADING", false).or(Conditions.textOf(state).equals("loading", false)),
                visible);
        assertFalse(visible.getValue());

        state.setValue("Loading");
        assertFalse(visible.getValue());

        state.setValue("LOADING");
        assertTrue(visible.getValue());

        state.setValue("loadinG");
        assertFalse(visible.getValue());

        state.setValue("loading");
        assertTrue(visible.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndCondition() {
        Property<String> state = new ObjectProperty<>("");
        Property<Boolean> visible = new ObjectProperty<>(false);

        Bindings.bindCondition(Conditions.textOf(state).check(s -> s.startsWith("A"))
                .and(Conditions.textOf(state).check(s -> s.endsWith("Z"))), visible);
        assertFalse(visible.getValue());

        state.setValue("Abuzz");
        assertFalse(visible.getValue());

        state.setValue("ABUZZ");
        assertTrue(visible.getValue());

        state.setValue("abuzZ");
        assertFalse(visible.getValue());

        state.setValue("AbuzZ");
        assertTrue(visible.getValue());
    }
}
