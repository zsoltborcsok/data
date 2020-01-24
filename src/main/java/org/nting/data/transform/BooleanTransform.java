package org.nting.data.transform;

import java.util.function.Function;

public class BooleanTransform<R> implements Function<Boolean, R> {

    private final R trueValue;
    private final R falseValue;

    public BooleanTransform(R trueValue, R falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public R apply(Boolean value) {
        if (value == Boolean.TRUE) {
            return trueValue;
        } else {
            return falseValue;
        }
    }
}
