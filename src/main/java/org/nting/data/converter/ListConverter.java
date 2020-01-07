package org.nting.data.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Converter;

public class ListConverter<A, B> extends Converter<List<A>, List<B>> {

    private final Converter<A, B> itemConverter;

    public ListConverter(Converter<A, B> itemConverter) {
        this.itemConverter = itemConverter;
    }

    @Override
    protected List<B> doForward(List<A> as) {
        return as.stream().map(itemConverter).collect(Collectors.toList());
    }

    @Override
    protected List<A> doBackward(List<B> bs) {
        return bs.stream().map(itemConverter.reverse()).collect(Collectors.toList());
    }
}
