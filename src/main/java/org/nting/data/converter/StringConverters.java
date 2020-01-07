package org.nting.data.converter;

import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public class StringConverters {

    public static Converter<String, Integer> intConverter() {
        return Ints.stringConverter();
    }

    public static Converter<String, Long> longConverter() {
        return Longs.stringConverter();
    }

    public static Converter<String, Float> floatConverter() {
        return Floats.stringConverter();
    }

    public static Converter<String, Double> doubleConverter() {
        return Doubles.stringConverter();
    }

    public static <T extends Enum<T>> Converter<String, T> enumConverter(Class<T> enumClass) {
        return Enums.stringConverter(enumClass);
    }
}
