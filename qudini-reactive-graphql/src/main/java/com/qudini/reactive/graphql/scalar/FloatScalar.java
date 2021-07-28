package com.qudini.reactive.graphql.scalar;

import graphql.language.FloatValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

/**
 * <p>Base class for float-based custom scalars.</p>
 * <p>Example:</p>
 * <pre><code>
 * &#64;Component
 * public class YourFloatBasedScalar extends FloatScalar&lt;YourJavaType&gt; {
 *
 *     public YourFloatBasedScalar() {
 *         super("YourScalarName", "Your scalar description", YourJavaType.class);
 *     }
 *
 *     &#64;Override
 *     public Float serialise(YourJavaType input) {
 *         return ...;
 *     }
 *
 *     &#64;Override
 *     public YourJavaType parse(Number input) {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 */
public abstract class FloatScalar<T> extends Scalar<T, Number, Float> {

    public FloatScalar(String name, String description, Class<T> type) {
        super(name, description, type);
    }

    @Override
    public final Float serialize(Object input) throws CoercingSerializeException {
        if (type.isInstance(input)) {
            return serialise((T) input);
        } else {
            throw unexpectedInstanceType(type, input);
        }
    }

    @Override
    public final T parseValue(Object input) throws CoercingParseValueException {
        if (input instanceof Number) {
            return parse((Number) input);
        } else {
            throw unexpectedInstanceType(Number.class, input);
        }
    }

    @Override
    public final T parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof FloatValue) {
            return parse(((FloatValue) input).getValue());
        } else {
            return null;
        }
    }

}
