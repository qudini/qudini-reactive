package com.qudini.reactive.graphql.app;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
public class MyFooBarResolver {

    @Bean
    public TypeRuntimeWiring myFooBar() {
        return newTypeWiring("MyFooBar").typeResolver(this::resolve).build();
    }

    private GraphQLObjectType resolve(TypeResolutionEnvironment env) {
        var instance = env.getObject();
        if (instance instanceof MyFoo) {
            return env.getSchema().getObjectType("MyFoo");
        } else if (instance instanceof MyBar) {
            return env.getSchema().getObjectType("MyBar");
        } else {
            throw new IllegalStateException("Expected " + instance + " to be instance of either MyFoo or MyBar");
        }
    }

}
