package com.qudini.reactive.graphql.app;

import com.qudini.reactive.graphql.scalar.FloatScalar;
import org.springframework.stereotype.Component;

@Component
public class MyFloatScalar extends FloatScalar<ScalarValue> {

    public MyFloatScalar() {
        super("MyFloat", ScalarValue.class);
    }

    @Override
    public Float serialise(ScalarValue input) {
        return input.getFloatValue();
    }

    @Override
    public ScalarValue parse(Number input) {
        return ScalarValue.BY_FLOAT.get(input.floatValue());
    }
}
