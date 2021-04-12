package com.qudini.reactive.graphql.app;

import com.qudini.reactive.graphql.scalar.BooleanScalar;
import org.springframework.stereotype.Component;

@Component
public class MyBooleanScalar extends BooleanScalar<ScalarValue> {

    public MyBooleanScalar() {
        super("MyBoolean", ScalarValue.class);
    }

    @Override
    public Boolean serialise(ScalarValue input) {
        return input.isBooleanValue();
    }

    @Override
    public ScalarValue parse(Boolean input) {
        return ScalarValue.BY_BOOLEAN.get(input);
    }

}
