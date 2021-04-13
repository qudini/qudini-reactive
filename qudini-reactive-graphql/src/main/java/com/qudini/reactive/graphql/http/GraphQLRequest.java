package com.qudini.reactive.graphql.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.ExecutionInput;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.dataloader.DataLoaderRegistry;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class GraphQLRequest {

    String query;
    Optional<String> operationName;
    Optional<Map<String, Object>> variables;

    @JsonCreator
    public static GraphQLRequest of(
            @JsonProperty("query") String query,
            @JsonProperty("operationName") Optional<String> operationName,
            @JsonProperty("variables") Optional<Map<String, Object>> variables
    ) {
        return new GraphQLRequest(query, operationName, variables);
    }

    public ExecutionInput toExecutionInput(ContextView context, DataLoaderRegistry registry) {
        var input = ExecutionInput
                .newExecutionInput()
                .query(query)
                .dataLoaderRegistry(registry)
                .context(context);
        operationName.ifPresent(input::operationName);
        variables.ifPresent(input::variables);
        return input.build();
    }

}
