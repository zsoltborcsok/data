package org.nting.data.converter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class StringToListConverter<T> extends Converter<String, List<T>> {

    public static StringToListConverter<String> stringToStringListConverter(String separator) {
        return new StringToListConverter<>(separator, Converter.identity());
    }

    private final Joiner joiner;
    private final Splitter splitter;
    private final Converter<String, T> converter;

    public StringToListConverter(String separator, Converter<String, T> converter) {
        joiner = Joiner.on(separator);
        splitter = Splitter.on(separator).omitEmptyStrings();
        this.converter = converter;
    }

    @Override
    protected List<T> doForward(String s) {
        return StreamSupport.stream(splitter.split(s).spliterator(), false).map(converter).collect(Collectors.toList());
    }

    @Override
    protected String doBackward(List<T> list) {
        return joiner.join(list.stream().map(converter.reverse()).collect(Collectors.toList()));
    }
}
