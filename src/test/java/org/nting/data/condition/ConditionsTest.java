package org.nting.data.condition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.nting.data.converter.StringToListConverter.stringToStringListConverter;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nting.data.Property;
import org.nting.data.Registration;
import org.nting.data.ValueChangeEvent;
import org.nting.data.ValueChangeListener;
import org.nting.data.model.Status;
import org.nting.data.property.ObjectProperty;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ConditionsTest {

    @Mock
    private ValueChangeListener<Boolean> vclFirst, vclSecond, vclThird;

    @Test
    public void testValueOf() {
        Property<Status> property = new ObjectProperty<>(Status.DEFERRED);
        Property<Status> otherProperty = new ObjectProperty<>(Status.IN_PROGRESS);

        Condition isIn = Conditions.valueOf(property).isIn(Status.COMPLETED, Status.DEFERRED);
        Condition isNotIn = Conditions.valueOf(property).isNotIn(Status.COMPLETED, Status.DEFERRED);
        Condition is = Conditions.valueOf(property).is(Status.DEFERRED);
        Condition isNot = Conditions.valueOf(property).isNot(Status.DEFERRED);
        Condition isNull = Conditions.valueOf(property).isNull();
        Condition isNotNull = Conditions.valueOf(property).isNotNull();
        Condition valueEquals = Conditions.valueOf(property).valueEquals(otherProperty);
        Condition valueNotEquals = Conditions.valueOf(property).valueNotEquals(otherProperty);
        Condition check = Conditions.valueOf(property).check(status -> !status.name().endsWith("ED"));

        assertTrue(isIn.getValue());
        assertFalse(isNotIn.getValue());
        assertTrue(is.getValue());
        assertFalse(isNot.getValue());
        assertFalse(isNull.getValue());
        assertTrue(isNotNull.getValue());
        assertFalse(valueEquals.getValue());
        assertTrue(valueNotEquals.getValue());
        assertFalse(check.getValue());

        Registration registration = valueEquals.addValueChangeListener(vclFirst);
        Registration registration2 = valueNotEquals.addValueChangeListener(vclSecond);
        otherProperty.setValue(Status.DEFERRED);
        assertTrue(valueEquals.getValue());
        assertFalse(valueNotEquals.getValue());
        verify(vclFirst).valueChange(any(ValueChangeEvent.class));
        verify(vclSecond).valueChange(any(ValueChangeEvent.class));
        reset(vclFirst, vclSecond);
        registration.remove();
        registration2.remove();

        property.setValue(Status.IN_PROGRESS);
        assertFalse(isIn.getValue());
        assertTrue(isNotIn.getValue());
        assertFalse(is.getValue());
        assertTrue(isNot.getValue());
        assertFalse(isNull.getValue());
        assertTrue(isNotNull.getValue());
        assertFalse(valueEquals.getValue());
        assertTrue(valueNotEquals.getValue());
        assertTrue(check.getValue());

        isNull.addValueChangeListener(vclFirst);
        isNotNull.addValueChangeListener(vclSecond);
        property.setValue(null);
        assertTrue(isNull.getValue());
        assertFalse(isNotNull.getValue());
        verify(vclFirst).valueChange(any(ValueChangeEvent.class));
        verify(vclSecond).valueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testTextOf() {
        Property<String> property = new ObjectProperty<>("anything");

        Condition equals = Conditions.textOf(property).equals("Anything", false);
        Condition equalsIgnoreCase = Conditions.textOf(property).equals("Anything", true);
        Condition matches = Conditions.textOf(property).matches("A.*g");
        Condition isBlank = Conditions.textOf(property).isBlank();
        Condition isNotBlank = Conditions.textOf(property).isNotBlank();
        Condition isEmpty = Conditions.textOf(property).isEmpty();
        Condition isNotEmpty = Conditions.textOf(property).isNotEmpty();

        assertFalse(equals.getValue());
        assertTrue(equalsIgnoreCase.getValue());
        assertFalse(matches.getValue());
        assertFalse(isBlank.getValue());
        assertTrue(isNotBlank.getValue());
        assertFalse(isEmpty.getValue());
        assertTrue(isNotEmpty.getValue());

        equals.addValueChangeListener(vclFirst);
        equalsIgnoreCase.addValueChangeListener(vclSecond);
        matches.addValueChangeListener(vclThird);

        property.setValue("Anything");
        assertTrue(equals.getValue());
        assertTrue(equalsIgnoreCase.getValue());
        assertTrue(matches.getValue());
        verify(vclFirst).valueChange(any(ValueChangeEvent.class));
        verify(vclSecond, never()).valueChange(any(ValueChangeEvent.class));
        verify(vclThird).valueChange(any(ValueChangeEvent.class));

        property.setValue(null);
        assertTrue(isEmpty.getValue());
        assertFalse(isNotEmpty.getValue());
        assertTrue(isBlank.getValue());
        assertFalse(isNotBlank.getValue());

        property.setValue("");
        assertTrue(isEmpty.getValue());
        assertFalse(isNotEmpty.getValue());
        assertTrue(isBlank.getValue());
        assertFalse(isNotBlank.getValue());

        property.setValue("   ");
        assertFalse(isEmpty.getValue());
        assertTrue(isNotEmpty.getValue());
        assertTrue(isBlank.getValue());
        assertFalse(isNotBlank.getValue());
    }

    @Test
    public void testItemsOf() {
        Property<String> stringProperty = new ObjectProperty<>("A, B, C");
        Property<List<String>> listProperty = stringProperty.transform(stringToStringListConverter(", "));

        Condition contains = Conditions.itemsOf(listProperty).contains("A");
        Condition notContains = Conditions.itemsOf(listProperty).notContains("A");
        Condition containsAll = Conditions.itemsOf(listProperty).containsAll("A", "B", "C");
        Condition containsAny = Conditions.itemsOf(listProperty).containsAny("A", "X");
        Condition containsNone = Conditions.itemsOf(listProperty).containsNone("X", "Y");
        Condition isEmpty = Conditions.itemsOf(listProperty).isEmpty();
        Condition isNotEmpty = Conditions.itemsOf(listProperty).isNotEmpty();
        Condition sizeIsEqualTo = Conditions.itemsOf(listProperty).sizeIsEqualTo(3);

        assertTrue(contains.getValue());
        assertFalse(notContains.getValue());
        assertTrue(containsAll.getValue());
        assertTrue(containsAny.getValue());
        assertTrue(containsNone.getValue());
        assertFalse(isEmpty.getValue());
        assertTrue(isNotEmpty.getValue());
        assertTrue(sizeIsEqualTo.getValue());

        contains.addValueChangeListener(vclFirst);
        containsAny.addValueChangeListener(vclSecond);
        containsNone.addValueChangeListener(vclThird);
        stringProperty.setValue("B, C");
        assertFalse(contains.getValue());
        assertTrue(notContains.getValue());
        assertFalse(containsAll.getValue());
        assertFalse(containsAny.getValue());
        assertTrue(containsNone.getValue());
        assertFalse(sizeIsEqualTo.getValue());
        verify(vclFirst).valueChange(any(ValueChangeEvent.class));
        verify(vclSecond).valueChange(any(ValueChangeEvent.class));
        verify(vclThird, never()).valueChange(any(ValueChangeEvent.class));

        stringProperty.setValue("B, C, X");
        assertFalse(contains.getValue());
        assertTrue(notContains.getValue());
        assertFalse(containsAll.getValue());
        assertTrue(containsAny.getValue());
        assertFalse(containsNone.getValue());
        assertTrue(sizeIsEqualTo.getValue());

        stringProperty.setValue("");
        assertFalse(contains.getValue());
        assertTrue(notContains.getValue());
        assertFalse(containsAll.getValue());
        assertFalse(containsAny.getValue());
        assertTrue(containsNone.getValue());
        assertTrue(isEmpty.getValue());
        assertFalse(isNotEmpty.getValue());
        assertFalse(sizeIsEqualTo.getValue());
    }

    @Test
    public void testConvert() {
        Property<Boolean> booleanProperty = new ObjectProperty<>(false);
        Condition condition = Conditions.convert(booleanProperty);

        assertFalse(condition.getValue());

        condition.addValueChangeListener(vclFirst);
        booleanProperty.setValue(true);
        assertTrue(condition.getValue());
        verify(vclFirst).valueChange(any(ValueChangeEvent.class));

        booleanProperty.addValueChangeListener(vclSecond);
        condition.setValue(false);
        assertFalse(booleanProperty.getValue());
        verify(vclSecond).valueChange(any(ValueChangeEvent.class));
    }
}