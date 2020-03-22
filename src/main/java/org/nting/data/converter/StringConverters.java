package org.nting.data.converter;

import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

@SuppressWarnings("UnstableApiUsage")
public class StringConverters {

    public static Converter<String, Integer> toIntConverter() {
        return Ints.stringConverter();
    }

    public static Converter<String, Long> toLongConverter() {
        return Longs.stringConverter();
    }

    public static Converter<String, Float> toFloatConverter() {
        return Floats.stringConverter();
    }

    public static Converter<String, Double> toDoubleConverter() {
        return Doubles.stringConverter();
    }

    public static <T extends Enum<T>> Converter<String, T> toEnumConverter(Class<T> enumClass) {
        return Enums.stringConverter(enumClass);
    }

    public static Converter<Integer, String> intConverter() {
        return toIntConverter().reverse();
    }

    public static Converter<Long, String> longConverter() {
        return toLongConverter().reverse();
    }

    public static Converter<Float, String> floatConverter() {
        return toFloatConverter().reverse();
    }

    public static Converter<Double, String> doubleConverter() {
        return toDoubleConverter().reverse();
    }

    public static <T extends Enum<T>> Converter<T, String> enumConverter(Class<T> enumClass) {
        return toEnumConverter(enumClass).reverse();
    }
}
