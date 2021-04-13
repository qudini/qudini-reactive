package com.qudini.reactive.graphql.http;

import com.qudini.gom.Gom;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.schema.GraphQLSchema;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Map;

import static com.qudini.reactive.utils.MoreTuples.onBoth;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RequiredArgsConstructor
public final class GraphQLHandler {

    private final Gom gom;
    private final GraphQLSchema schema;

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
                .instrumentation(new DataLoaderDispatcherInstrumentation())
                .build();
        return Mono
                .fromFuture(() -> graphql.executeAsync(input))
                .map(ExecutionResult::toSpecification);
    }

    private Mono<ServerResponse> respond(Mono<Map<String, Object>> body) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(body, ParameterizedTypeReference.forType(Map.class));
    }

}
