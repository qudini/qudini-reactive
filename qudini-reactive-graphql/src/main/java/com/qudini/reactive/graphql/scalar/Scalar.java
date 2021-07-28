package com.qudini.reactive.graphql.scalar;

import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Scalar<T, U, V extends U> implements Coercing<T, V> {

    protected final String name;
    protected final String description;
    protected final Class<T> type;

    public final GraphQLScalarType build() {
        return GraphQLScalarType
                .newScalar()
                .name(name)
                .description(description)
                .coercing(this)
                .build();
    }

    public abstract V serialise(T input);

    public abstract T parse(U input);

    protected final CoercingSerializeException unexpectedInstanceType(Class<?> type, Object value) throws CoercingSerializeException {
        var message = "Expected " + value + " to be of type " + type.getSimpleName()
                + (value == null ? "" : " but is of type " + value.getClass().getSimpleName() + " instead");
        return new CoercingSerializeException(message);
    }

}
