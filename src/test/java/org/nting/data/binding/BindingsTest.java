package org.nting.data.binding;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nting.data.Property;
import org.nting.data.binding.Bindings.BindingStrategy;
import org.nting.data.property.ObjectProperty;

import com.google.common.base.Converter;

public class BindingsTest {

    @Test
    public void testBind_Read() {
        Property<String> source = new ObjectProperty<>("A");
        Property<String> target = new ObjectProperty<>("");

        Bindings.bind(BindingStrategy.READ, source, target);
        assertEquals("A", target.getValue());

        source.setValue("B");
        assertEquals("B", target.getValue());

        target.setValue("C");
        assertEquals("B", source.getValue());
    }

    @Test
    public void testBind_ReadWrite() {
        Property<String> source = new ObjectProperty<>("A");
        Property<String> target = new ObjectProperty<>("");

        Bindings.bind(BindingStrategy.READ_WRITE, source, target);
        assertEquals("A", target.getValue());

        source.setValue("B");
        assertEquals("B", target.getValue());

        target.setValue("C");
        assertEquals("C", source.getValue());
    }

    @Test
    public void testBindWithConverter_Read() {
        Property<String> source = new ObjectProperty<>("A");
        Property<String> target = new ObjectProperty<>("");

        Converter<String, String> converter = Converter.from(s -> s + s, t -> {
            throw new UnsupportedOperationException();
        });

        Bindings.bind(BindingStrategy.READ, source, target, converter);
        assertEquals("AA", target.getValue());

        source.setValue("B");
        assertEquals("BB", target.getValue());

        target.setValue("C");
        assertEquals("B", source.getValue());
    }

    @Test
    public void testBindWithConverter_ReadWrite() {
        Property<String> source = new ObjectProperty<>("A");
        Property<String> target = new ObjectProperty<>("");

        Converter<String, String> converter = Converter.from(s -> s + "1",
                t -> t != null ? t.substring(0, t.length() - 1) : null);

        Bindings.bind(BindingStrategy.READ_WRITE, source, target, converter);
        assertEquals("A1", target.getValue());

        source.setValue("B");
        assertEquals("B1", target.getValue());

        target.setValue("C1");
        assertEquals("C", source.getValue());
    }

    @Test
    public void testConditionalBinding() {
        Property<String> billingAddress = new ObjectProperty<>("6782, City Street No.");
        Property<String> deliveryAddress = new ObjectProperty<>("");
        Property<Boolean> sameAddress = new ObjectProperty<>(false);

        Bindings.conditionalBinding(billingAddress, deliveryAddress, sameAddress);
        assertEquals("6782, City Street No.", billingAddress.getValue());
        assertEquals("", deliveryAddress.getValue());

        sameAddress.setValue(true);
        assertEquals("6782, City Street No.", billingAddress.getValue());
        assertEquals("6782, City Street No.", deliveryAddress.getValue());

        deliveryAddress.setValue("6782, City Street 12.");
        assertEquals("6782, City Street No.", billingAddress.getValue());
        assertEquals("6782, City Street 12.", deliveryAddress.getValue());

        billingAddress.setValue("6782, City Street Number");
        assertEquals("6782, City Street Number", billingAddress.getValue());
        assertEquals("6782, City Street Number", deliveryAddress.getValue());

        sameAddress.setValue(false);
        billingAddress.setValue("6782, City Street No.");
        assertEquals("6782, City Street No.", billingAddress.getValue());
        assertEquals("6782, City Street Number", deliveryAddress.getValue());
    }
}
