package com.qudini.reactive.graphql;

import com.qudini.gom.Converters;
import com.qudini.gom.Gom;
import com.qudini.gom.TypeResolver;
import com.qudini.reactive.graphql.http.GraphQLHandler;
import com.qudini.reactive.graphql.scalar.Scalar;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import lombok.SneakyThrows;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReactiveGraphQLAutoConfiguration {

    @Bean
    public Gom gom(ListableBeanFactory listableBeanFactory) {
        var resolvers = listableBeanFactory
                .getBeansWithAnnotation(TypeResolver.class)
                .values();
        var converters = Converters
                .newConverters(Context.class)
                .converter(Mono.class, (mono, context) -> mono.contextWrite(context).toFuture())
                .converter(Flux.class, (flux, context) -> flux.collect(toUnmodifiableList()))
                .build();
        return Gom
                .newGom()
                .resolvers(resolvers)
                .converters(converters)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public TypeDefinitionRegistry graphqlRegistry(@Value("classpath:schema.graphql") Resource schema) {
        try (var reader = new BufferedReader(new InputStreamReader(schema.getInputStream()))) {
            return new SchemaParser().parse(reader);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeWiring graphqlWiring(Gom gom, Collection<Scalar<?, ?, ?>> scalars, Collection<TypeRuntimeWiring> wirings) {
        var wiring = RuntimeWiring.newRuntimeWiring();
        scalars.stream().map(Scalar::build).forEach(wiring::scalar);
        wirings.forEach(wiring::type);
        gom.decorateRuntimeWiringBuilder(wiring);
        return wiring.build();
    }

    @Bean
    public GraphQLSchema graphqlSchema(TypeDefinitionRegistry registry, RuntimeWiring wiring) {
        return new SchemaGenerator().makeExecutableSchema(registry, wiring);
    }

    @Bean
    public GraphQLHandler graphqlHandler(Gom gom, GraphQLSchema graphqlSchema) {
        return new GraphQLHandler(gom, graphqlSchema);
    }

    @Bean
    public RouterFunction<ServerResponse> graphqlRouter(GraphQLHandler graphqlHandler) {
        return route(
                POST("/graphql").and(contentType(APPLICATION_JSON)),
                graphqlHandler::postJson
        );
    }

}
