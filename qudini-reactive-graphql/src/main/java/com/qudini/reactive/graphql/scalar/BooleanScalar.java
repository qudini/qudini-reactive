package com.qudini.reactive.graphql.scalar;

import graphql.language.BooleanValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

/**
 * <p>Base class for boolean-based custom scalars.</p>
 * <p>Example:</p>
 * <pre><code>
 * &#64;Component
 * public class YourBooleanBasedScalar extends BooleanScalar&lt;YourJavaType&gt; {
 *
 *     public YourBooleanBasedScalar() {
 *         super("YourScalarName", YourJavaType.class);
 *     }
 *
 *     &#64;Override
 *     public Boolean serialise(YourJavaType input) {
 *         return ...;
 *     }
 *
 *     &#64;Override
 *     public YourJavaType parse(Boolean input) {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 */
public abstract class BooleanScalar<T> extends Scalar<T, Boolean, Boolean> {

    public BooleanScalar(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public final Boolean serialize(Object input) throws CoercingSerializeException {
        if (type.isInstance(input)) {
            return serialise((T) input);
        } else {
            throw unexpectedInstanceType(type, input);
        }
    }

    @Override
    public final T parseValue(Object input) throws CoercingParseValueException {
        if (input instanceof Boolean) {
            return parse((Boolean) input);
        } else {
            throw unexpectedInstanceType(Boolean.class, input);
        }
    }

    @Override
    public final T parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof BooleanValue) {
            return parse(((BooleanValue) input).isValue());
        } else {
            return null;
        }
    }

}
