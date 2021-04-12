package com.qudini.reactive.graphql.app;

import com.qudini.reactive.graphql.scalar.StringScalar;
import org.springframework.stereotype.Component;

@Component
public class MyStringScalar extends StringScalar<ScalarValue> {

    public MyStringScalar() {
        super("MyString", ScalarValue.class);
    }

    @Override
    public String serialise(ScalarValue input) {
        return input.getStringValue();
    }

    @Override
    public ScalarValue parse(String input) {
        return ScalarValue.BY_STRING.get(input);
    }
}
