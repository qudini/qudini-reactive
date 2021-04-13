package com.qudini.reactive.graphql.app;

import com.qudini.reactive.graphql.scalar.IntScalar;
import org.springframework.stereotype.Component;

@Component
public class MyIntScalar extends IntScalar<ScalarValue> {

    public MyIntScalar() {
        super("MyInt", ScalarValue.class);
    }

    @Override
    public Integer serialise(ScalarValue input) {
        return input.getIntValue();
    }

    @Override
    public ScalarValue parse(Number input) {
        return ScalarValue.BY_INT.get(input.intValue());
    }

}
