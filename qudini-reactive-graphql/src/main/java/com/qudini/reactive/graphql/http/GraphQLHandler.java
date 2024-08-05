package com.qudini.reactive.graphql.http;

import com.qudini.gom.Gom;
import com.qudini.reactive.logging.Log;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Arrays;
import java.util.Map;

import static com.qudini.utils.MoreTuples.onBoth;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
public final class GraphQLHandler {

    private final Gom gom;
    private final GraphQLSchema schema;
    private final DataFetcherExceptionHandler exceptionHandler;

    @Value("${qudini-reactive.graphql-max-depth:5}")
    private Integer maxDepth;

    public Mono<ServerResponse> postJson(ServerRequest request) {
        return Mono
                .deferContextual(Mono::just)
                .zipWith(request.bodyToMono(GraphQLRequest.class))
                .flatMap(onBoth(this::execute))
                .transform(this::respond);
    }

    private Mono<Map<String, Object>> execute(ContextView context, GraphQLRequest request) {
        var registry = new DataLoaderRegistry();
        gom.decorateDataLoaderRegistry(registry);
        var input = request.toExecutionInput(context, registry);
        var graphql = GraphQL
                .newGraphQL(schema)
                .instrumentation(instrumentation())
                .defaultDataFetcherExceptionHandler(exceptionHandler)
                .build();
        return Log
                .thenFuture(() -> graphql.executeAsync(input))
                .map(ExecutionResult::toSpecification);
    }

    public Instrumentation instrumentation() {
        return new ChainedInstrumentation(
                Arrays.asList(
                        new MaxQueryDepthInstrumentation(maxDepth), // Limit query depth to maxDepth
                        new MaxQueryComplexityInstrumentation(maxDepth) // Limit query complexity to maxDepth
                )
        );
    }

    private Mono<ServerResponse> respond(Mono<Map<String, Object>> body) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(body, ParameterizedTypeReference.forType(Map.class));
    }

}
