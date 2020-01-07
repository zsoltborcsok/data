package org.nting.data.converter;

import java.util.List;

import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StringToListConverter extends Converter<String, List<String>> {

    private final Joiner joiner;
    private final Splitter splitter;

    public StringToListConverter(String separator) {
        joiner = Joiner.on(separator);
        splitter = Splitter.on(separator);
    }

    @Override
    protected List<String> doForward(String s) {
        return Lists.newLinkedList(splitter.split(s));
    }

    @Override
    protected String doBackward(List<String> strings) {
        return joiner.join(strings);
    }
}
