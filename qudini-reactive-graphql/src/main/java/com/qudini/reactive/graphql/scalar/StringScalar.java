package com.qudini.reactive.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

/**
 * <p>Base class for string-based custom scalars.</p>
 * <p>Example:</p>
 * <pre><code>
 * &#64;Component
 * public class YourStringBasedScalar extends StringScalar&lt;YourJavaType&gt; {
 *
 *     public YourStringBasedScalar() {
 *         super("YourScalarName", "Your scalar description", YourJavaType.class);
 *     }
 *
 *     &#64;Override
 *     public String serialise(YourJavaType input) {
 *         return ...;
 *     }
 *
 *     &#64;Override
 *     public YourJavaType parse(String input) {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 */
public abstract class StringScalar<T> extends Scalar<T, String, String> {

    public StringScalar(String name, String description, Class<T> type) {
        super(name, description, type);
    }

    @Override
    public final String serialize(Object input) throws CoercingSerializeException {
        if (type.isInstance(input)) {
            return serialise((T) input);
        } else {
            throw unexpectedInstanceType(type, input);
        }
    }

    @Override
    public final T parseValue(Object input) throws CoercingParseValueException {
        if (input instanceof String) {
            return parse((String) input);
        } else {
            throw unexpectedInstanceType(String.class, input);
        }
    }

    @Override
    public final T parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            return parse(((StringValue) input).getValue());
        } else {
            return null;
        }
    }

    public String serialise(T input) {
        return input.toString();
    }

}
