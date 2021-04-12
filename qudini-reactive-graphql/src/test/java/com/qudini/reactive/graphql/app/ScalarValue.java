package com.qudini.reactive.graphql.app;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

@RequiredArgsConstructor
@Getter
public enum ScalarValue {

    FOO(false, "foo", -1, -1.1F),
    BAR(true, "bar", 1, 1.1F);

    public static final Map<Boolean, ScalarValue> BY_BOOLEAN = Stream.of(values()).collect(toUnmodifiableMap(ScalarValue::isBooleanValue, identity()));
    public static final Map<String, ScalarValue> BY_STRING = Stream.of(values()).collect(toUnmodifiableMap(ScalarValue::getStringValue, identity()));
    public static final Map<Integer, ScalarValue> BY_INT = Stream.of(values()).collect(toUnmodifiableMap(ScalarValue::getIntValue, identity()));
    public static final Map<Float, ScalarValue> BY_FLOAT = Stream.of(values()).collect(toUnmodifiableMap(ScalarValue::getFloatValue, identity()));

    private final boolean booleanValue;
    private final String stringValue;
    private final int intValue;
    private final float floatValue;

}
