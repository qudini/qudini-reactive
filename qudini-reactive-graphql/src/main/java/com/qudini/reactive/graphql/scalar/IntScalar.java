package com.qudini.reactive.graphql.scalar;

import graphql.language.IntValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

/**
 * <p>Base class for int-based custom scalars.</p>
 * <p>Example:</p>
 * <pre><code>
 * &#64;Component
 * public class YourIntBasedScalar extends IntScalar&lt;YourJavaType&gt; {
 *
 *     public YourIntBasedScalar() {
 *         super("YourScalarName", YourJavaType.class);
 *     }
 *
 *     &#64;Override
 *     public Integer serialise(YourJavaType input) {
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
public abstract class IntScalar<T> extends Scalar<T, Number, Integer> {

    public IntScalar(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public final Integer serialize(Object input) throws CoercingSerializeException {
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
        if (input instanceof IntValue) {
            return parse(((IntValue) input).getValue());
        } else {
            return null;
        }
    }

}
